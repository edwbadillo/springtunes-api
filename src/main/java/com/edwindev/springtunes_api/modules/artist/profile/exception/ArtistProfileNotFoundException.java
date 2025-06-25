package com.edwindev.springtunes_api.modules.artist.profile.exception;

import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.common.exception.ResourceNotFoundException;

public class ArtistProfileNotFoundException extends ResourceNotFoundException {

    public ArtistProfileNotFoundException() {
        super(ErrorCode.NOT_FOUND, "Artist not found.");
    }

    public ArtistProfileNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
