package com.edwindev.springtunes_api.common.exception;

import lombok.Getter;

/**
 * Base class for all exceptions in the application.
 */
@Getter
public abstract class CommonException extends RuntimeException {

    /**
     * The error code associated with the exception.
     */
    private final ErrorCode errorCode;

    public CommonException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public CommonException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
