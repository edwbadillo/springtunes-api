package com.edwindev.springtunes_api.config.security;

import com.edwindev.springtunes_api.common.dto.ErrorResponse;
import com.edwindev.springtunes_api.common.exception.AuthException;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthenticated requests.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        String message = "Unauthorized";

        // TODO: This not working as expected, never gets AuthException

        if (authException instanceof AuthException customEx) {
            errorCode = customEx.getErrorCode();
            message = customEx.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(errorCode, message);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
