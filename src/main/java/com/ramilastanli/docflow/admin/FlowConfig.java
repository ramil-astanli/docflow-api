package com.ramilastanli.docflow.admin;

import com.ramilastanli.docflow.core.entity.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FlowConfig {

    @Bean
    public MessageChannel adminActivityChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow adminDashboardFlow(SimpMessagingTemplate messagingTemplate) {
        return IntegrationFlow.from(adminActivityChannel())
                .handle(message -> {
                    if (message.getPayload() instanceof Document doc) {
                        Map<String, Object> adminPayload = new HashMap<>();
                        adminPayload.put("event", "REALTIME_UPDATE");
                        adminPayload.put("documentId", doc.getId());
                        adminPayload.put("status", doc.getStatus().name());
                        adminPayload.put("title", doc.getTitle());
                        adminPayload.put("timestamp", LocalDateTime.now().toString());

                        messagingTemplate.convertAndSend("/topic/admin/activity", (Object) adminPayload);
                    }
                })
                .get();
    }
}