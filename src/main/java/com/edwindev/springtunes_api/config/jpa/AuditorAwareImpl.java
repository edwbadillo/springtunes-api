package com.edwindev.springtunes_api.config.jpa;


import com.edwindev.springtunes_api.auth.AppUserDetails;
import com.edwindev.springtunes_api.modules.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Custom implementation of AuditorAware that returns the
 * currently authenticated user.
 */
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AppUserDetails authUserDetails) {
            return Optional.of(authUserDetails.getUser());
        }

        return Optional.empty();
    }
}
