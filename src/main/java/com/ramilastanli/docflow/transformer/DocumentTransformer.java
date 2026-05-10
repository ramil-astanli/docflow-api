package com.ramilastanli.docflow.transformer;

import com.ramilastanli.docflow.core.repository.DocumentConfigRepository;
import com.ramilastanli.docflow.core.dto.request.DocumentRequestDto;
import com.ramilastanli.docflow.core.entity.ApprovalStep;
import com.ramilastanli.docflow.core.entity.ConfigApprover;
import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.core.entity.DocumentConfig;
import com.ramilastanli.docflow.core.entity.enums.ApprovalStatus;
import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentTransformer {

    private final DocumentConfigRepository configRepository;

    @Transformer
    public Document toEntity(DocumentRequestDto dto) {
        DocumentConfig config = configRepository.findById(dto.getType())
                .orElseThrow(() -> new RuntimeException("Sistemdə belə bir sənəd tipi konfiqurasiya edilməyib: " + dto.getType()));

        List<ConfigApprover> configApprovers = config.getDefaultApprovers();

        if (configApprovers == null || configApprovers.size() != config.getRequiredApprovals()) {
            throw new RuntimeException(String.format(
                    "Konfiqurasiya xətası: %s tipi üçün %d təsdiqçi tələb olunur, lakin %d nəfər təyin edilib!",
                    dto.getType(),
                    config.getRequiredApprovals(),
                    (configApprovers != null ? configApprovers.size() : 0)
            ));
        }

        Document document = new Document();
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setType(dto.getType());
        document.setSubmitterEmail(dto.getSubmitterEmail());
        document.setStatus(DocumentStatus.PENDING_APPROVAL);
        document.setPriority(dto.getPriority());
        document.setFilePath(dto.getFilePath());
        document.setRequiredApprovals(config.getRequiredApprovals());

        List<ApprovalStep> steps = new ArrayList<>();

        for (ConfigApprover ca : configApprovers) {
            ApprovalStep step = new ApprovalStep();
            step.setDocument(document);
            step.setStepOrder(ca.getStepOrder());
            step.setApproverEmail(ca.getApproverEmail());

            if (ca.getStepOrder() == 1) {
                step.setStatus(ApprovalStatus.PENDING);
            } else {
                step.setStatus(ApprovalStatus.WAITING);
            }

            steps.add(step);
        }

        document.setApprovalSteps(steps);
        return document;
    }
}