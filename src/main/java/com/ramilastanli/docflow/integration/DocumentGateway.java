package com.ramilastanli.docflow.integration;

import com.ramilastanli.docflow.core.dto.request.DocumentRequestDto;
import com.ramilastanli.docflow.core.entity.Document;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface DocumentGateway {
    @Gateway(requestChannel = "documentInputChannel")
    Document startWorkFlow(DocumentRequestDto documentRequestDto);
}
