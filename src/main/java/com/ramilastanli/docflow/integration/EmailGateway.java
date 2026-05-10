package com.ramilastanli.docflow.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface EmailGateway {
    @Gateway(requestChannel = "emailNotificationChannel")
    void sendMailNotification(Long Id);
}
