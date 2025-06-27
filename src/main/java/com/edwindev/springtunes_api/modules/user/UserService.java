package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.auth.AppUserDetails;
import com.edwindev.springtunes_api.modules.user.dto.AuthenticatedUserData;
import com.edwindev.springtunes_api.modules.user.dto.UserCreateData;

/**
 * Defines the methods for working with user entities.
 */
public interface UserService {

    /**
     * Creates a new user based on the provided data if it is
     * verified and does not already exist in the SQL database.
     *
     * @param data The data for creating the user.
     * @return The created user details.
     */
    AppUserDetails createVerifiedUser(UserCreateData data);

    /**
     * Retrieves the authenticated user's data.
     *
     * @return {@link AuthenticatedUserData}
     */
    AuthenticatedUserData getAuthenticatedUserData();
}
