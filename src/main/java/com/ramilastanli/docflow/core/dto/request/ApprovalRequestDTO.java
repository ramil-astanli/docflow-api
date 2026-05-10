package com.ramilastanli.docflow.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApprovalRequestDTO(
        @NotNull Long documentId,

        @NotBlank
        String documentType,
        @NotBlank
        String decision,

        String comment
) {}