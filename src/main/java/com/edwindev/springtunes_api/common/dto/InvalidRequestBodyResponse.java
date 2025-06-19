package com.edwindev.springtunes_api.common.dto;

import java.util.List;

/**
 * Represents an invalid request body to be sent to the client.
 */
public record InvalidRequestBodyResponse(
        String message,
        List<InvalidData> errors
) {
}
