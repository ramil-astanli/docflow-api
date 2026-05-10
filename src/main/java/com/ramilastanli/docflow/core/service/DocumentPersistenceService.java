package com.ramilastanli.docflow.core.service;

import com.ramilastanli.docflow.core.entity.ApprovalStep;
import com.ramilastanli.docflow.core.entity.AuditLog;
import com.ramilastanli.docflow.core.entity.ConfigApprover;
import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.core.entity.enums.ApprovalStatus;
import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import com.ramilastanli.docflow.core.repository.AuditRepository;
import com.ramilastanli.docflow.core.repository.ConfigApproverRepository;
import com.ramilastanli.docflow.core.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentPersistenceService {

    private final DocumentRepository documentRepository;
    private final ConfigApproverRepository configApproverRepository;
    private final AuditRepository auditLogRepository;

    @Transactional
    public Document saveAndInitiate(Document document) {
        log.info("Sənəd emal edilir: {}, Tip: {}", document.getTitle(), document.getType());

        if (document.getId() == null) {
            document.setStatus(DocumentStatus.SUBMITTED);
            createApprovalSteps(document);

            document = documentRepository.save(document);

            AuditLog firstLog = AuditLog.builder()
                    .documentId(document.getId())
                    .action("DOCUMENT_SUBMITTED")
                    .newStatus(document.getStatus().name())
                    .performedBy(document.getSubmitterEmail())
                    .comment("Sənəd sistemə yükləndi. Təsdiq zənciri yaradıldı: " + document.getRequiredApprovals() + " addım.")
                    .build();

            auditLogRepository.save(firstLog);

            log.info("Yeni sənəd və ilk audit qeydi yaradıldı. ID: {}", document.getId());
        }

        document.setStatus(DocumentStatus.PENDING_APPROVAL);
        Document savedDoc = documentRepository.save(document);

        auditLogRepository.save(AuditLog.builder()
                .documentId(savedDoc.getId())
                .action("WORKFLOW_STARTED")
                .oldStatus(DocumentStatus.SUBMITTED.name())
                .newStatus(DocumentStatus.PENDING_APPROVAL.name())
                .performedBy("SYSTEM")
                .comment("Sənəd təsdiq mərhələsinə keçdi.")
                .build());

        return savedDoc;
    }

    private void createApprovalSteps(Document document) {
        List<ConfigApprover> approverConfigs = configApproverRepository.findByDocumentConfigDocumentTypeOrderByStepOrderAsc(document.getType());

        if (approverConfigs.isEmpty()) {
            log.warn("{} tipi üçün konfiqurasiya tapılmadı, DEFAULT yoxlanılır...", document.getType());
            approverConfigs = configApproverRepository.findByDocumentConfigDocumentTypeOrderByStepOrderAsc("DEFAULT");
        }

        if (approverConfigs.isEmpty()) {
            throw new RuntimeException("Sistemdə bu sənəd tipi üçün təsdiqçi təyin edilməyib!");
        }

        List<ApprovalStep> steps = new ArrayList<>();

        for (ConfigApprover config : approverConfigs) {
            ApprovalStep step = new ApprovalStep();
            step.setDocument(document);
            step.setStepOrder(config.getStepOrder());
            step.setApproverEmail(config.getApproverEmail());

            if (config.getStepOrder() == 1) {
                step.setStatus(ApprovalStatus.PENDING);
            } else {
                step.setStatus(ApprovalStatus.WAITING);
            }
            steps.add(step);
        }

        document.setApprovalSteps(steps);
        document.setRequiredApprovals(steps.size());
    }
}