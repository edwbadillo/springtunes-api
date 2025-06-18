package com.edwindev.springtunes_api.config;

import com.edwindev.springtunes_api.common.dto.ErrorResponse;
import com.edwindev.springtunes_api.common.dto.InvalidField;
import com.edwindev.springtunes_api.common.dto.ValidationErrorResponse;
import com.edwindev.springtunes_api.common.dto.ValidationErrorsResponse;
import com.edwindev.springtunes_api.common.exception.AuthException;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.common.exception.InvalidDataException;
import com.edwindev.springtunes_api.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<InvalidField> invalidFields = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            FieldError fieldError = (FieldError) error;
            invalidFields.add(new InvalidField(
                    fieldError.getCode(),
                    fieldError.getDefaultMessage(),
                    fieldError.getField(),
                    fieldError.getRejectedValue()
            ));
        });

        return new ResponseEntity<>(new ValidationErrorsResponse(invalidFields), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleInvalidDataException(InvalidDataException e) {
        return new ValidationErrorResponse(new InvalidField(
                e.getType(),
                e.getMessage(),
                e.getField(),
                e.getValue()
        ));
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthException(AuthException e) {
        return new ErrorResponse(e.getErrorCode(), e.getMessage());
    }
}
