package com.edwindev.springtunes_api.modules.user.dto;

/**
 * Shows the details of a user authenticated.
 */
public record AuthenticatedUserData(
        String id,
        String displayName,
        String email,
        String profilePictureUrl,
        String role
) {
}
