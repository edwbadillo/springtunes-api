package com.edwindev.springtunes_api.common.exception;

/**
 * Defines error codes for exceptions.
 */
public enum ErrorCode {

    UNAUTHORIZED("AUTH_000", "User is not authorized."),
    INVALID_TOKEN("AUTH_001", "Invalid or expired token."),
    CREATE_USER("AUTH_003", "User could not be created in the database."),
    EMAIL_NOT_VERIFIED("AUTH_004", "The user's email is not verified."),
    FORM_VALIDATION("FORM_001", "Invalid form data."),
    VALIDATION_ERROR("FORM_002", "Invalid data."),
    UNIQUE_ERROR("FORM_409", "Value must be unique."),
    INVALID_VALUE("FORM_422", "Value is invalid."),
    NOT_FOUND("SYS_404", "Resource not found."),
    INTERNAL_ERROR("SYS_000", "An unexpected error occurred.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
