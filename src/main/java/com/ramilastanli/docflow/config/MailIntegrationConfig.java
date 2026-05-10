package com.ramilastanli.docflow.config;

import com.ramilastanli.docflow.core.repository.DocumentRepository;
import com.ramilastanli.docflow.core.entity.enums.ApprovalStatus;
import com.ramilastanli.docflow.core.entity.AuditLog;
import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.core.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.mail.outbound.MailSendingMessageHandler;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class MailIntegrationConfig {

    private final DocumentRepository documentRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuditRepository auditLogRepository;

    @Bean
    @ServiceActivator(inputChannel = "sendMailChannel", adviceChain = "retryAdvice")
    public MessageHandler mailOutboundAdapter(JavaMailSender mailSender) {
        return new MailSendingMessageHandler(mailSender);
    }

    @ServiceActivator(inputChannel = "emailNotificationChannel")
    public void sendApprovalWebSocket(Long documentId) {
        documentRepository.findByIdWithSteps(documentId).ifPresent(doc -> {
            doc.getApprovalSteps().stream()
                    .filter(s -> s.getStatus() == ApprovalStatus.PENDING)
                    .findFirst()
                    .ifPresent(step -> {
                        messagingTemplate.convertAndSendToUser(
                                step.getApproverEmail(),
                                "/queue/notifications",
                                "Sizə yeni təsdiq sorğusu gəldi: " + doc.getTitle());

                        auditLogRepository.save(AuditLog.builder()
                                .documentId(doc.getId())
                                .action("NOTIFICATION_SENT")
                                .newStatus(doc.getStatus().name())
                                .performedBy("SYSTEM")
                                .comment("Təsdiqçi " + step.getApproverEmail() + " üçün bildiriş göndərildi. Addım: " + step.getStepOrder())
                                .build());
                    });
        });
    }

    @ServiceActivator(inputChannel = "rejectionNotificationChannel")
    public void sendRejectionWebSocket(Object result) {
        String signal = (String) result;
        Long docId = Long.parseLong(signal.split(":")[1]);
        documentRepository.findById(docId).ifPresent(doc -> {
            messagingTemplate.convertAndSendToUser(
                    doc.getSubmitterEmail(),
                    "/queue/status",
                    "Təəssüf ki, sənədiniz rədd edildi: " + doc.getTitle());

            auditLogRepository.save(AuditLog.builder()
                    .documentId(doc.getId())
                    .action("REJECTION_NOTIFIED")
                    .newStatus("REJECTED")
                    .performedBy("SYSTEM")
                    .comment("İstifadəçiyə sənədin rədd edilməsi barədə bildiriş göndərildi.")
                    .build());
        });
    }


    @Transformer(inputChannel = "emailNotificationChannel", outputChannel = "sendMailChannel")
    public SimpleMailMessage enrichAndTransform(Long documentId) {
        Document document = documentRepository.findByIdWithSteps(documentId)
                .orElseThrow(() -> new RuntimeException("Sənəd tapılmadı: " + documentId));

        var activeStep = document.getApprovalSteps().stream()
                .filter(step -> step.getStatus() == ApprovalStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aktiv mərhələ tapılmadı"));

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(activeStep.getApproverEmail());
        mail.setSubject("Yeni Təsdiq Sorğusu: " + document.getTitle());
        mail.setText(String.format("Salam,\n\n%s tipli sənəd yaradıldı.", document.getType()));
        mail.setFrom("no-reply@docflow.com");
        return mail;
    }

    @Transformer(inputChannel = "rejectionNotificationChannel", outputChannel = "sendMailChannel")
    public SimpleMailMessage transformRejectionMail(Object result) {
        String signal = (String) result;
        Long docId = Long.parseLong(signal.split(":")[1]);
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Sənəd tapılmadı"));

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(document.getSubmitterEmail());
        mail.setSubject("Sənədiniz İmtina Edildi: " + document.getTitle());
        mail.setText("Təəssüf ki, sənədiniz rədd edildi.");
        mail.setFrom("no-reply@docflow.com");
        return mail;
    }

    @ServiceActivator(inputChannel = "errorChannel")
    public void handleFailedEmails(Message<MessagingException> errorMessage) {
        MessagingException exception = errorMessage.getPayload();
        var failedMessage = exception.getFailedMessage();

        auditLogRepository.save(AuditLog.builder()
                .action("MAIL_DELIVERY_FAILED")
                .performedBy("SYSTEM_MAILER")
                .comment("KRİTİK: Email göndərilməsi uğursuz oldu! Səbəb: " + exception.getMessage())
                .build());

        System.err.println("CRITICAL: Email göndərilməsi 3 cəhddən sonra tamamilə uğursuz oldu!");
        System.err.println("Xəta səbəbi: " + (exception.getCause() != null ? exception.getCause().getMessage() : "Bilinmir"));

        messagingTemplate.convertAndSend("/topic/admin-alerts",
                "KRİTİK XƏTA: Email sistemi çatımlı deyil! " +
                        (exception.getCause() != null ? exception.getCause().getMessage() : "SMTP xətası"));

        documentRepository.findAll().stream()
                .findFirst()
                .ifPresent(anyDoc -> {
                    String adminNotifyLog = String.format(
                            "ADMIN ALERT: Sənəd bildirişi uğursuz oldu. Payload: %s. Səbəb: %s",
                            failedMessage.getPayload(),
                            exception.getMessage()
                    );

                    System.out.println("Sistem: Admin bazadakı qeydlər vasitəsilə məlumatlandırıldı.");
                    System.out.println("Log qeydi: " + adminNotifyLog);
                });

        log.error("Dead Letter Channel (DLC) işə düşdü. Mesaj zay oldu: {}", failedMessage.getPayload());
    }
}