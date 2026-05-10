package com.ramilastanli.docflow.integration;

import com.ramilastanli.docflow.core.dto.request.ApprovalRequestDTO;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface ApprovalGateway {
    @Gateway(requestChannel = "approvalChannel")
    void sendToApprovalFlow(Message<ApprovalRequestDTO> message);
}