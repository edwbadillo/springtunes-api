package com.edwindev.springtunes_api.common.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * The response when a resource is created.
 */
@Builder
@Getter
public class ResourceCreatedResponse<T> extends SimpleResponse {
    private final T data;

    public ResourceCreatedResponse(String message, T data) {
        super(message);
        this.data = data;
    }

    public ResourceCreatedResponse(T data) {
        super("Resource created successfully.");
        this.data = data;
    }
}
