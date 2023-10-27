package com.intuit.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.intuit.api.dto.ApiResponse;
import com.intuit.services.utils.ErrorResponse;

/*
@ControllerAdvice annotation is that when an exception gets thrown in any controllers (like CustomValidationException), Spring looks for any @ExceptionHandler methods that can handle that exception. If it finds one, it uses that method to produce the response.

when a CustomValidationException is thrown in the controllers, the handleCustomValidationException method is called. Within this method, an ErrorResponse object is constructed and returning it with a BAD_REQUEST status.
*/
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomValidationException(CustomValidationException exc) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exc.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exc) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "User is not admin, so can't view users.",
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DataAccessException exc) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Database error occurred.",
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiResponse apiResponse = new ApiResponse(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ UserNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
        ApiResponse apiResponse = new ApiResponse(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exc) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                exc.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // @ExceptionHandler(RuntimeException.class)
    // public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException
    // exc) {
    // ErrorResponse error = new ErrorResponse(
    // HttpStatus.INTERNAL_SERVER_ERROR.value(),
    // "An unexpected runtime error occurred.",
    // System.currentTimeMillis());
    // return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    // }

    // @ExceptionHandler({ Exception.class })
    // public ResponseEntity<Object> handleGenericException(Exception ex) {
    // ApiResponse apiResponse = new ApiResponse("An unexpected exception
    // occurred.");
    // return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    // }

}
