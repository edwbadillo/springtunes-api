package com.edwindev.springtunes_api.common.dto;

import com.edwindev.springtunes_api.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse extends SimpleResponse {
    private final String code;

    public ErrorResponse(String message) {
        super(message);
        this.code = ErrorCode.INTERNAL_ERROR.code();
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.code();
    }
}
