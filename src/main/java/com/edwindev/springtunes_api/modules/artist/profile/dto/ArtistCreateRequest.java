package com.edwindev.springtunes_api.modules.artist.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The request for creating an artist profile.
 *
 * @param artistName the name of the artist
 * @param bio        the bio of the artist, can be null
 */
public record ArtistCreateRequest(
        @NotBlank(message = "Artist name is required.")
        @Size(max = 100, message = "Artist name must not exceed 100 characters.")
        String artistName,

        @Size(max = 500, message = "Bio must not exceed 500 characters.")
        String bio
) {
}
