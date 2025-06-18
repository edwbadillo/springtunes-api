package com.edwindev.springtunes_api.modules.user.exception;

import com.edwindev.springtunes_api.common.exception.AuthException;
import com.edwindev.springtunes_api.common.exception.ErrorCode;

/**
 * Thrown when a user cannot be created.
 */
public class UserCreateException extends AuthException {

    public UserCreateException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
