package com.edwindev.springtunes_api.modules.music.genre.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The request for creating a genre.
 *
 * @param name        the genre name
 * @param description the genre description
 */
public record GenreRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description
) {
}
