package com.ramilastanli.docflow.util;

import com.ramilastanli.docflow.config.ApplicationConstants;
import com.ramilastanli.docflow.entity.User;
import com.ramilastanli.docflow.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationUtility {

    public static String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Əgər sistem avtomatik bir iş görürsə (məsələn, zamanlanmış tapşırıq və ya 
        // Spring Integration-ın daxili prosesi), giriş edən yoxdursa "SYSTEM" qaytarırıq.
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return ApplicationConstants.SYSTEM;
        }

        Object principal = authentication.getPrincipal();

        // CustomUserDetails istifadə ediriksə (tövsiyə olunan budur)
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername(); // Bizim halda bu email qaytaracaq
        }

        // Birbaşa User entity-si ilə işləyiriksə
        if (principal instanceof User user) {
            return user.getEmail();
        }

        return principal.toString();
    }
}