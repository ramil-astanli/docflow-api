package com.ramilastanli.docflow.core.repository;

import com.ramilastanli.docflow.core.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.approvalSteps WHERE d.id = :id")
    Optional<Document> findByIdWithSteps(@Param("id") Long id);
}
