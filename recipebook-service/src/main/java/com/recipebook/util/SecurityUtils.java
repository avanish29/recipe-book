package com.recipebook.util;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author - AvanishKishorPandey
 */
public final class SecurityUtils {
    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     * @return the login of the current user
     */
    public static String getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((UserDetails)securityContext.getAuthentication().getPrincipal()).getUsername();
    }
}
