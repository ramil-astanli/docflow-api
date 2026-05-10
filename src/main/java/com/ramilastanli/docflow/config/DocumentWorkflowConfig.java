package com.ramilastanli.docflow.config;

import com.ramilastanli.docflow.core.service.DocumentPersistenceService;
import com.ramilastanli.docflow.core.dto.request.DocumentRequestDto;
import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.transformer.DocumentTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocumentWorkflowConfig {

    private final DocumentTransformer documentTransformer;

    @Bean
    public IntegrationFlow mainDocumentFlow(DocumentPersistenceService documentPersistenceService) {
        return IntegrationFlow.from("documentInputChannel")
                .filter(DocumentRequestDto.class,
                        dto -> dto.getTitle() != null && !dto.getTitle().isBlank(),
                        f -> f.discardChannel("errorChannel"))

                .transform(documentTransformer)

                .handle(documentPersistenceService, "saveAndInitiate")

                .wireTap(flow -> flow
                                .handle(m -> {
                                    Document doc = (Document) m.getPayload();
                                    log.info("Sənəd yaradıldı: ID={}, Tip={}, Tələb olunan təsdiq={}",
                                            doc.getId(), doc.getType(), doc.getRequiredApprovals());
                                })
                )

                .<Document, Long>transform(Document::getId)
                .channel("emailNotificationChannel")
                .get();
    }
}