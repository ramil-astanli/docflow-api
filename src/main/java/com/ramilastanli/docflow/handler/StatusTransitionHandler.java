package com.ramilastanli.docflow.handler;

import com.ramilastanli.docflow.core.repository.DocumentRepository;
import com.ramilastanli.docflow.core.entity.ApprovalStep;
import com.ramilastanli.docflow.core.entity.AuditLog;
import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.core.entity.enums.ApprovalStatus;
import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import com.ramilastanli.docflow.core.repository.AuditRepository;
import com.ramilastanli.docflow.core.dto.request.ApprovalRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component("statusTransitionHandler")
@RequiredArgsConstructor
@Slf4j
public class StatusTransitionHandler {

    private final DocumentRepository documentRepository;
    private final AuditRepository auditRepository;

    @Transactional
    public Object processTransition(ApprovalRequestDTO request, String currentUserEmail) {
        log.info("Qərar emal edilir: DocID={}, Decision={}, User={}",
                request.documentId(), request.decision(), currentUserEmail);

        Document document = documentRepository.findById(request.documentId())
                .orElseThrow(() -> new RuntimeException("Sənəd tapılmadı: " + request.documentId()));

        ApprovalStep currentStep = document.getApprovalSteps().stream()
                .filter(step -> step.getStatus() == ApprovalStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aktiv təsdiq addımı tapılmadı!"));

        if (!currentStep.getApproverEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new RuntimeException("Sizin bu sənəd üzərində qərar vermək səlahiyyətiniz yoxdur!");
        }

        auditRepository.save(AuditLog.builder()
                .documentId(document.getId())
                .action(request.decision() + " by " + currentUserEmail)
                .comment(request.comment())
                .build());

        if ("REJECT".equalsIgnoreCase(request.decision())) {
            currentStep.setStatus(ApprovalStatus.REJECTED);
            document.setStatus(DocumentStatus.REJECTED);
            documentRepository.save(document);

            log.warn("Sənəd imtina edildi. Submitter: {}", document.getSubmitterEmail());

            return "REJECTED_ID:" + document.getId();
        }

        currentStep.setStatus(ApprovalStatus.APPROVED);

        Optional<ApprovalStep> nextStepOpt = document.getApprovalSteps().stream()
                .filter(step -> step.getStepOrder() == currentStep.getStepOrder() + 1)
                .findFirst();

        if (nextStepOpt.isPresent()) {
            ApprovalStep nextStep = nextStepOpt.get();
            nextStep.setStatus(ApprovalStatus.PENDING);
            documentRepository.save(document);

            log.info("Növbəti təsdiq mərhələsinə keçildi: {}", nextStep.getApproverEmail());
            return document.getId(); // Long qaytarılır -> Növbəti mail gedir
        } else {
            document.setStatus(DocumentStatus.APPROVED);
            documentRepository.save(document);

            log.info("Sənəd tam təsdiqləndi! Flow başa çatdı.");
            return null;
        }
    }
}