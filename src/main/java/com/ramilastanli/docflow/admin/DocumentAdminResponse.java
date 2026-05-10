package com.ramilastanli.docflow.admin;

import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import com.ramilastanli.docflow.core.entity.enums.DocumentPriority;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DocumentAdminResponse {
    private Long id;
    private String title;
    private String type;
    private DocumentStatus status;
    private DocumentPriority priority;
    private String submitterEmail;
    private LocalDateTime createdAt;
}