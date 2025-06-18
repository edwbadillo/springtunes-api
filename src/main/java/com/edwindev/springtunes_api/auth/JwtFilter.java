package com.edwindev.springtunes_api.auth;

import com.edwindev.springtunes_api.common.exception.AuthException;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.config.security.FirebaseAuthService;
import com.edwindev.springtunes_api.modules.user.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final FirebaseAuthService firebaseAuth;
    private final AppUserDetailsService userDetailsService;
    private final UserService userService;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7);
            FirebaseToken decodedToken = null;
            try {
                decodedToken = firebaseAuth.verifyToken(idToken);
                if (!decodedToken.isEmailVerified())
                    throw new AuthException(ErrorCode.EMAIL_NOT_VERIFIED);

                String uid = decodedToken.getUid();

                UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
                setAuthenticated(userDetails);
            } catch (FirebaseAuthException e) {
                String message = e.getErrorCode().name() + " - " + e.getMessage();
                log.error(message);
                throw new AuthException(ErrorCode.FIREBASE_ERROR, message);
            } catch (UsernameNotFoundException e) {
                log.warn("User is verified but not found in the database, creating new user...");
                createUserForVerifiedUser(decodedToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void createUserForVerifiedUser(FirebaseToken decodedToken) {
        AppUserDetails createdUser = userService.createVerifiedUser(decodedToken);
        setAuthenticated(createdUser);
    }

    private void setAuthenticated(UserDetails userDetails) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
