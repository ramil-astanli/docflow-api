package com.ramilastanli.docflow.config;

import com.ramilastanli.docflow.handler.StatusTransitionHandler;
import com.ramilastanli.docflow.core.dto.request.ApprovalRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;

@Configuration
@RequiredArgsConstructor
public class ApprovalWorkflowConfig {

    @Bean
    public IntegrationFlow approvalFlow(StatusTransitionHandler handler) {
        return IntegrationFlow.from("approvalChannel")
                .log(LoggingHandler.Level.INFO, "ApprovalFlow", m -> {
                    ApprovalRequestDTO dto = (ApprovalRequestDTO) m.getPayload();
                    String userEmail = m.getHeaders().get("currentUserEmail", String.class);
                    return String.format("Qərar emal edilir: DocID=%d, Qərar=%s, İstifadəçi=%s",
                            dto.documentId(), dto.decision(), userEmail);
                })
                .<ApprovalRequestDTO>handle((payload, headers) -> {
                    String userEmail = headers.get("currentUserEmail", String.class);
                    return handler.processTransition(payload, userEmail);
                })
                .<Object, String>route(result -> {
                    if (result instanceof Long) {
                        return "emailNotificationChannel";
                    }
                    else if (result instanceof String && ((String) result).startsWith("REJECTED_ID:")) {
                        return "rejectionNotificationChannel";
                    }
                    return "nullChannel";
                })
                .get();
    }
}