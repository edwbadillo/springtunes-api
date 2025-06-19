package com.edwindev.springtunes_api.common.dto;

import com.edwindev.springtunes_api.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ValidationErrorResponse extends ErrorResponse {
    private final InvalidData error;

    public ValidationErrorResponse(InvalidData error) {
        super(ErrorCode.VALIDATION_ERROR, ErrorCode.FORM_VALIDATION.message());
        this.error = error;
    }
}
