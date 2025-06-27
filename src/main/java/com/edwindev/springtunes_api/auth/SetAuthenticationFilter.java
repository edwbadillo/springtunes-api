package com.edwindev.springtunes_api.auth;

import com.edwindev.springtunes_api.modules.user.UserService;
import com.edwindev.springtunes_api.modules.user.dto.UserCreateData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that sets the authentication in the SecurityContextHolder
 * after the OAuth2 filter.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SetAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        checkAuthentication();
        filterChain.doFilter(request, response);
    }

    private void checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated())
            return;
        if (authentication instanceof JwtAuthenticationToken jwtAuth)
            getOrCreateUser(jwtAuth);
    }

    private void getOrCreateUser(JwtAuthenticationToken jwtAuth) {
        Jwt jwt = jwtAuth.getToken();
        String sub = jwt.getClaimAsString("sub");

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(sub);
            setAuthenticated(userDetails);
        } catch (UsernameNotFoundException e) {
            String email = jwt.getClaimAsString("email");
            UserCreateData data = UserCreateData.builder()
                    .userID(sub)
                    .email(email)
                    .build();
            createUserForVerifiedUser(data);
        }
    }

    private void createUserForVerifiedUser(UserCreateData data) {
        log.warn("User is verified but not found in the database, creating new user...");
        AppUserDetails createdUser = userService.createVerifiedUser(data);
        setAuthenticated(createdUser);
    }

    private void setAuthenticated(UserDetails userDetails) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
