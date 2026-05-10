package com.ramilastanli.docflow.admin;

import com.ramilastanli.docflow.core.entity.Document;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface AdminDashboardGateway {

    @Gateway(requestChannel = "adminActivityChannel")
    void sendToDashboard(Document document);
}