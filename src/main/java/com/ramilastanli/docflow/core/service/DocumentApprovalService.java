package com.ramilastanli.docflow.core.service;

import com.ramilastanli.docflow.integration.ApprovalGateway;
import com.ramilastanli.docflow.core.dto.request.ApprovalRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentApprovalService {

    private final ApprovalGateway approvalGateway;

    public void submitApproval(ApprovalRequestDTO requestDTO, String currentUserEmail) {
        log.info("Təsdiq prosesi başladılır. DocID: {}, Approver: {}",
                requestDTO.documentId(), currentUserEmail);

        Message<ApprovalRequestDTO> message = MessageBuilder
                .withPayload(requestDTO)
                .setHeader("currentUserEmail", currentUserEmail)
                .build();

        approvalGateway.sendToApprovalFlow(message);
        log.info("Təsdiq mesajı (payload + header) flow-a uğurla daxil edildi.");
    }
}