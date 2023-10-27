package com.intuit.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException() {
        super("User already exists!");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    // Optionally, you can also add other constructors or properties if needed
}
