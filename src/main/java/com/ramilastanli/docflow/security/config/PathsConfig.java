package com.ramilastanli.docflow.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class PathsConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of(
                "/api/auth/**",           // Login və Register endpointləri
                "/swagger-ui/**",         // API sənədləşməsi
                "/v3/api-docs/**"
        );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths() {
        return List.of(
                "/api/documents/**",      // Sənəd yükləmə və siyahılama
                "/api/approvals/**"       // Təsdiq/İmtina əməliyyatları
        );
    }

    @Bean(name = "adminPaths")
    public List<String> adminPaths() {
        return List.of(
                "/api/admin/**",          // Admin paneli
                "/api/audit-logs/**"      // Audit tarixçəsinə baxış
        );
    }
}