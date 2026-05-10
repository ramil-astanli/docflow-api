package com.ramilastanli.docflow.admin;

import com.ramilastanli.docflow.core.entity.Document;
import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentSpecification {

    public static Specification<Document> getDocumentsByFilters(DocumentStatus status, String title, String submitterEmail) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (submitterEmail != null && !submitterEmail.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("submitterEmail")), "%" + submitterEmail.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}