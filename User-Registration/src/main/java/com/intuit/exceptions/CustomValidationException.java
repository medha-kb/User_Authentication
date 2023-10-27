package com.intuit.exceptions;

import org.springframework.http.HttpStatus;

public class CustomValidationException extends RuntimeException {

    private HttpStatus status = HttpStatus.BAD_REQUEST;;

    public CustomValidationException(String message) {
        super(message);
    }

    public CustomValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
