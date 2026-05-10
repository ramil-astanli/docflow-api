package com.ramilastanli.docflow.core.service;

import com.ramilastanli.docflow.core.repository.DocumentConfigRepository;
import com.ramilastanli.docflow.core.entity.DocumentConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentConfigService {
    private final DocumentConfigRepository repository;

    public Integer getRequiredApprovals(String type) {
        return repository.findById(type)
                .map(DocumentConfig::getRequiredApprovals)
                .orElse(1);
    }
}