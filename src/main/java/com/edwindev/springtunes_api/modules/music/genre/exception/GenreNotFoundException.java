package com.edwindev.springtunes_api.modules.music.genre.exception;

import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.common.exception.ResourceNotFoundException;

public class GenreNotFoundException extends ResourceNotFoundException {
    public GenreNotFoundException() {
        super(ErrorCode.NOT_FOUND, "Genre music not found.");
    }

    public GenreNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
