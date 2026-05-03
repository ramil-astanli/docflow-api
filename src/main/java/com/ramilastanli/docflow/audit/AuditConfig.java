package com.ramilastanli.docflow.audit;

import com.ramilastanli.docflow.config.ApplicationConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // 1. Mövcud sessiyanı (authentication) götürürük
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 2. Əgər giriş edilməyibsə və ya anonim istifadəçidirsə (məsələn, Register zamanı)
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of(ApplicationConstants.SYSTEM); // "SYSTEM" qayıdır
            }

            // 3. Giriş etmiş istifadəçinin emailini qaytarırıq
            return Optional.ofNullable(authentication.getName());
        };
    }
}