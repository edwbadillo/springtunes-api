package com.edwindev.springtunes_api.common.exception;

import com.edwindev.springtunes_api.common.dto.InvalidData;
import lombok.Getter;

/**
 * Thrown when invalid request data (request body or URL parameters) is provided.
 */
@Getter
public class InvalidDataException extends IllegalArgumentException {
    private final InvalidData errorData;

    public InvalidDataException(InvalidData errorData) {
        super(errorData.message());
        this.errorData = errorData;
    }
}
