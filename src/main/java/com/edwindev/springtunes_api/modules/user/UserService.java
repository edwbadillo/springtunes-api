package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.auth.AppUserDetails;
import com.edwindev.springtunes_api.modules.user.dto.AuthenticatedUserData;
import com.google.firebase.auth.FirebaseToken;

/**
 * Defines the methods for working with user entities.
 */
public interface UserService {

    /**
     * Creates a new user based on the provided Firebase token if it is
     * verified and does not already exist in the SQL database.
     *
     * @param firebaseToken The Firebase token to create the user from.
     * @return The created user details.
     */
    AppUserDetails createVerifiedUser(FirebaseToken firebaseToken);

    /**
     * Retrieves the authenticated user's data.
     *
     * @return {@link AuthenticatedUserData}
     */
    AuthenticatedUserData getAuthenticatedUserData();
}
