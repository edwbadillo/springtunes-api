package com.edwindev.springtunes_api.common.exception;

/**
 * Thrown when a resource is not found.
 */
public class ResourceNotFoundException extends CommonException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
