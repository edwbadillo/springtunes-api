package com.edwindev.springtunes_api.config.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Defines the methods for working with Firebase authentication.
 */
@Service
@RequiredArgsConstructor
public class FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;

    /**
     * Verifies a Firebase ID token.
     *
     * @param idToken The ID token to verify.
     * @return The verified token with firebase user information.
     * @throws IllegalArgumentException If the ID token is invalid.
     * @throws FirebaseAuthException    If there is an error verifying the token.
     */
    public FirebaseToken verifyToken(String idToken) throws IllegalArgumentException, FirebaseAuthException {
        return firebaseAuth.verifyIdToken(idToken);
    }
}
