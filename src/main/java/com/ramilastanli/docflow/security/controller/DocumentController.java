package com.ramilastanli.docflow.security.controller;

import com.ramilastanli.docflow.core.service.DocumentApprovalService;
import com.ramilastanli.docflow.core.service.DocumentService;
import com.ramilastanli.docflow.core.dto.request.DocumentRequestDto;
import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.core.dto.request.ApprovalRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentApprovalService approvalService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> create(
            @RequestPart("data") DocumentRequestDto dto,
            @RequestPart("file") MultipartFile file) throws IOException {

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        dto.setSubmitterEmail(currentUserEmail);

        Document createdDocument = documentService.createDocument(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    @PostMapping("/approve")
    public ResponseEntity<String> approve(@RequestBody @Valid ApprovalRequestDTO dto) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        approvalService.submitApproval(dto, currentUserEmail);

        return ResponseEntity.accepted().body("Təsdiq qərarınız qəbul edildi və emal olunur.");
    }
}
