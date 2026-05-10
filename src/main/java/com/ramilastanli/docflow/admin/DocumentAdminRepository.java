package com.ramilastanli.docflow.admin;

import com.ramilastanli.docflow.core.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentAdminRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    @Query("SELECT d.status, COUNT(d) FROM Document d GROUP BY d.status")
    List<Object[]> countDocumentsByStatus();

    long countByCreatedAtAfter(LocalDateTime dateTime);
}