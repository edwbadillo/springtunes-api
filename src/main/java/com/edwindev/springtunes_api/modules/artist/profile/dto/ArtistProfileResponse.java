package com.edwindev.springtunes_api.modules.artist.profile.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The response for getting an artist profile.
 *
 * @param id                the ID of the artist profile
 * @param artistName        the name of the artist
 * @param bio               the bio of the artist
 * @param status            the status of the artist profile
 * @param profilePictureUrl the URL of the profile picture (Firebase Storage)
 * @param hasUser           whether the artist profile has a user (has been claimed)
 * @param createdAt         the creation date of the artist profile
 */
@Builder
public record ArtistProfileResponse(
        UUID id,
        String artistName,
        String bio,
        String status,
        String profilePictureUrl,
        boolean hasUser,
        LocalDateTime createdAt
) {
}
