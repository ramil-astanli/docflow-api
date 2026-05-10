package com.ramilastanli.docflow.util;

import com.ramilastanli.docflow.common.util.ApplicationConstants;
import com.ramilastanli.docflow.core.entity.User;
import com.ramilastanli.docflow.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationUtility {

    public static String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return ApplicationConstants.SYSTEM;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername();
        }

        if (principal instanceof User user) {
            return user.getEmail();
        }

        return principal.toString();
    }
}