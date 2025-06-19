package com.edwindev.springtunes_api.common.dto;

import lombok.Builder;

/**
 * Represents invalid data (request body or URL parameters)
 *
 * @param type    the error type, should be one of
 *                {@link com.edwindev.springtunes_api.common.exception.ErrorCode}
 * @param message the error message
 * @param field   the field that caused the error
 * @param value   the value that caused the error
 */
@Builder
public record InvalidData(
        String type,
        String message,
        String field,
        Object value
) {
}
