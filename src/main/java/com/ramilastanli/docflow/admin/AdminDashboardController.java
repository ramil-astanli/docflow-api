package com.ramilastanli.docflow.admin;

import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final DocumentAdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/documents")
    public ResponseEntity<Page<DocumentAdminResponse>> searchDocuments(
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(adminService.getFilteredDocuments(status, title, email, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam DocumentStatus status,
            Principal principal) {

        String adminEmail = principal.getName();
        adminService.updateDocumentStatus(id, status, adminEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export-history")
    public ResponseEntity<byte[]> exportWorkflowHistory(@RequestParam String format) throws Exception {
        byte[] data = adminService.exportAuditLogs(format);

        String extension = format.equalsIgnoreCase("csv") ? "csv" : "json";
        String contentType = format.equalsIgnoreCase("csv") ? "text/csv" : "application/json";
        String fileName = "workflow_history_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + "." + extension;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }
}