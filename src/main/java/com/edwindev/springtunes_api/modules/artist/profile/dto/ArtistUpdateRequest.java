package com.edwindev.springtunes_api.modules.artist.profile.dto;

import jakarta.validation.constraints.Size;

public record ArtistUpdateRequest(
        @Size(max = 500, message = "Bio must not exceed 500 characters.")
        String bio,

        String profilePictureUrl
) {
}
