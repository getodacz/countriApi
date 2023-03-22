package com.example.takehome.exception;

/**
 * Exception for invalid input data for the main api endpoints.
 */
public class ApiInputDataValidationException extends RuntimeException {
    public ApiInputDataValidationException(String errorMessage) {
        super("Your request could not be processed. " + errorMessage);
    }
}
