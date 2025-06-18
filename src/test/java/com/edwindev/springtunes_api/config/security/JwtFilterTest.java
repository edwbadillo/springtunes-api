package com.edwindev.springtunes_api.config.security;

import com.edwindev.springtunes_api.auth.AppUserDetails;
import com.edwindev.springtunes_api.auth.AppUserDetailsService;
import com.edwindev.springtunes_api.auth.JwtFilter;
import com.edwindev.springtunes_api.common.exception.AuthException;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.modules.user.UserService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private FirebaseAuthService firebaseAuth;

    @Mock
    private AppUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(firebaseAuth, userDetailsService, userService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetAuthentication_whenTokenIsValidAndUserExists() throws Exception {
        String token = "valid-token";
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        AppUserDetails userDetails = mock(AppUserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(firebaseAuth.verifyToken(token)).thenReturn(decodedToken);
        when(decodedToken.isEmailVerified()).thenReturn(true);
        when(decodedToken.getUid()).thenReturn("uid123");

        when(userDetailsService.loadUserByUsername("uid123")).thenReturn(userDetails);

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(userDetails);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldThrowAuthException_whenEmailNotVerified() throws Exception {
        String token = "token";
        FirebaseToken decodedToken = mock(FirebaseToken.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(firebaseAuth.verifyToken(token)).thenReturn(decodedToken);
        when(decodedToken.isEmailVerified()).thenReturn(false);

        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_NOT_VERIFIED);

        verifyNoInteractions(userDetailsService, userService);
    }

    @Test
    void shouldCreateUser_whenUserNotFound() throws Exception {
        String token = "token";
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        AppUserDetails newUser = mock(AppUserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(firebaseAuth.verifyToken(token)).thenReturn(decodedToken);
        when(decodedToken.isEmailVerified()).thenReturn(true);
        when(decodedToken.getUid()).thenReturn("uid123");

        when(userDetailsService.loadUserByUsername("uid123")).thenThrow(new UsernameNotFoundException("not found"));
        when(userService.createVerifiedUser(decodedToken)).thenReturn(newUser);

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(newUser);
    }

    @Test
    void shouldIgnore_whenNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}

