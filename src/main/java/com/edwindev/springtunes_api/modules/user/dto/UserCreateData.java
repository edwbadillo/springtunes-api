package com.edwindev.springtunes_api.modules.user.dto;

import lombok.Builder;

/**
 * The data for creating a user.
 *
 * @param userID a unique user ID
 * @param email  the email of the user, will be used as the display name
 */
@Builder
public record UserCreateData(
        String userID,
        String email
) {
}
