package com.edwindev.springtunes_api.common.dto;

import lombok.Getter;

/**
 * Represents a simple message response to be sent to the client.
 */
@Getter
public class SimpleResponse {
    private final String message;

    public SimpleResponse(String message) {
        this.message = message;
    }
}
