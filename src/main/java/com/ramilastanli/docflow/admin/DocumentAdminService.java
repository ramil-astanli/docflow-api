package com.ramilastanli.docflow.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramilastanli.docflow.core.entity.AuditLog;
import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import com.ramilastanli.docflow.core.repository.AuditRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentAdminService {

    private final DocumentAdminRepository documentRepository;
    private final AdminDashboardGateway adminGateway;
    private final AuditRepository auditLogRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        Map<String, Long> statusDistribution = documentRepository.countDocumentsByStatus().stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1],
                        (existing, replacement) -> existing
                ));

        stats.put("statusCounts", statusDistribution);
        stats.put("totalDocuments", documentRepository.count());

        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        stats.put("last24HoursActivity", documentRepository.countByCreatedAtAfter(oneDayAgo));

        return stats;
    }

    @Transactional(readOnly = true)
    public Page<DocumentAdminResponse> getFilteredDocuments(DocumentStatus status, String title, String email, Pageable pageable) {
        Specification<Document> spec = DocumentSpecification.getDocumentsByFilters(status, title, email);

        return documentRepository.findAll(spec, pageable)
                .map(doc -> DocumentAdminResponse.builder()
                        .id(doc.getId())
                        .title(doc.getTitle())
                        .type(doc.getType())
                        .status(doc.getStatus())
                        .priority(doc.getPriority())
                        .submitterEmail(doc.getSubmitterEmail())
                        .createdAt(doc.getCreatedAt())
                        .build());
    }

    @Transactional
    public void updateDocumentStatus(Long id, DocumentStatus newStatus, String adminEmail) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sənəd tapılmadı"));

        String oldStatus = doc.getStatus().name();

        doc.setStatus(newStatus);
        documentRepository.save(doc);

        AuditLog log = AuditLog.builder()
                .documentId(doc.getId())
                .action("STATUS_UPDATE")
                .oldStatus(oldStatus)
                .newStatus(newStatus.name())
                .performedBy(adminEmail)
                .comment("Status " + oldStatus + " vəziyyətindən " + newStatus + " vəziyyətinə dəyişdirildi.")
                .build();

        auditLogRepository.save(log);

        adminGateway.sendToDashboard(doc);
    }

    public byte[] exportAuditLogs(String format) throws Exception {
        List<AuditLog> logs = auditLogRepository.findAll();

        if ("json".equalsIgnoreCase(format)) {
            return new ObjectMapper().findAndRegisterModules().writeValueAsBytes(logs);
        }

        StringBuilder csv = new StringBuilder("ID,DocID,Action,OldStatus,NewStatus,PerformedBy,Time,Comment\n");
        for (AuditLog log : logs) {
            csv.append(log.getId()).append(",")
                    .append(log.getDocumentId()).append(",")
                    .append(log.getAction()).append(",")
                    .append(log.getOldStatus()).append(",")
                    .append(log.getNewStatus()).append(",")
                    .append(log.getPerformedBy()).append(",")
                    .append(log.getCreatedAt()).append(",")
                    .append("\"").append(log.getComment()).append("\"\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}