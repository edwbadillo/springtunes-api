package com.edwindev.springtunes_api.common.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * Thrown when the user's email is not verified. Users must verify their email
 * before they can log in.
 */
@Getter
public class AuthException extends AuthenticationException {
    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
