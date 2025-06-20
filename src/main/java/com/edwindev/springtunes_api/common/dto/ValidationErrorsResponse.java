package com.edwindev.springtunes_api.common.dto;

import com.edwindev.springtunes_api.common.exception.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationErrorsResponse extends ErrorResponse {
    private final List<InvalidData> errors;

    public ValidationErrorsResponse(List<InvalidData> errors) {
        super(ErrorCode.FORM_VALIDATION, ErrorCode.FORM_VALIDATION.message());
        this.errors = errors;
    }
}
