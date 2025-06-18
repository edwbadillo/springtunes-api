package com.edwindev.springtunes_api.common.dto;

/**
 * Represents an invalid data response to be sent to the client.
 */
public record InvalidField(
        String type,
        String message,
        String field,
        Object value
) {
}
