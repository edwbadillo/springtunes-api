package com.edwindev.springtunes_api.modules.artist.profile.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/**
 * The request for updating the status of an artist profile.
 *
 * @param id     the ID of the artist profile
 * @param status the new status of the artist profile, must be one of
 *               {@link com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile.Status}
 */
public record ArtistStatusRequest(
        UUID id,

        @NotBlank
        String status
) {
}
