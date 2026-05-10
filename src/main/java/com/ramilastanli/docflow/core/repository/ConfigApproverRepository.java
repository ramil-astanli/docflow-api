package com.ramilastanli.docflow.core.repository;


import com.ramilastanli.docflow.core.entity.ConfigApprover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigApproverRepository extends JpaRepository<ConfigApprover, Long> {

    List<ConfigApprover> findByDocumentConfigDocumentTypeOrderByStepOrderAsc(String documentType);
}