package com.ramilastanli.docflow.common.util;

import com.ramilastanli.docflow.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static Long getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof CustomUserDetails)
                .map(principal -> ((CustomUserDetails) principal).getId())
                .orElseThrow(() -> new RuntimeException("Sistemə giriş edilməyib və ya sessiya vaxtı bitib."));
    }
}