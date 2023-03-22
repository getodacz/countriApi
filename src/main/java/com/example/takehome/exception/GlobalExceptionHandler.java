package com.example.takehome.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;

/**
 The GlobalExceptionHandler class is a Spring Controller Advice that provides centralized handling of exceptions across
 the application. It contains methods for handling specific exceptions and returning appropriate HTTP responses with error messages.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     Handles RequestNotPermitted exceptions and returns an HTTP response with a TOO_MANY_REQUESTS status code and error message.
     @param ex The RequestNotPermitted exception that occurred.
     @param request The WebRequest associated with the exception.
     @return An HTTP response with a TOO_MANY_REQUESTS status code and an ErrorMessage object containing an error message.
     */
    @ExceptionHandler({RequestNotPermitted.class})
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<ErrorMessage> handleRequestNotPermitted(RequestNotPermitted ex, WebRequest request) {
        log.error("Too many requests to the API");
        String internalServerErrorMessage =
            "Sorry, we couldn't complete your request at this time. " +
            "The server has received more requests than the allowed limit. " +
            "Please try again later or contact support if the problem persists.";

        ErrorMessage message = new ErrorMessage(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                new Date(),
                internalServerErrorMessage,
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.TOO_MANY_REQUESTS);
    }
    /**

     Handles UsernameNotFoundException exceptions and returns an HTTP response with a NOT_FOUND status code and error message.
     @param ex The UsernameNotFoundException exception that occurred.
     @param request The WebRequest associated with the exception.
     @return An HTTP response with a NOT_FOUND status code and an ErrorMessage object containing an error message.
     */
    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ErrorMessage> usernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        log.debug("UsernameNotFoundException occurred: ", ex);
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                "The request could not be completed. Username was not found.",
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
    /**
     Handles BadCredentialsException exceptions and returns an HTTP response with an UNAUTHORIZED status code and error message.
     @param ex The BadCredentialsException exception that occurred.
     @param request The WebRequest associated with the exception.
     @return An HTTP response with an UNAUTHORIZED status code and an ErrorMessage object containing an error message.
     */
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ErrorMessage> badCredentialsException(BadCredentialsException ex, WebRequest request) {
        log.debug("BadCredentialsException occurred: ", ex);
        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                "The user could not be authenticated due to incorrect credentials.",
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     Handles IllegalStateException exceptions and returns an HTTP response with a BAD_REQUEST status code and error message.
     @param ex The IllegalStateException exception that occurred.
     @param request The WebRequest associated with the exception.
     @return An HTTP response with a BAD_REQUEST status code and an ErrorMessage object containing an error message.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> duplicateUserException(IllegalStateException ex, WebRequest request) {
        log.debug("IllegalStateException occurred: ", ex);
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                "The request could not be completed. An user with the same email already exists.",
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     Handles ApiInputDataValidationException exceptions and returns an HTTP response with a BAD_REQUEST status code and error message.
     @param ex The ApiInputDataValidationException exception that occurred.
     @param request The WebRequest associated with the exception.
     @return An HTTP response with a BAD_REQUEST status code and an ErrorMessage object containing an error message.
     */
    @ExceptionHandler(ApiInputDataValidationException.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(ApiInputDataValidationException ex, WebRequest request) {
        log.error("A validation exception occurred: ", ex);

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     Handles all other exceptions and returns an HTTP response with an INTERNAL_SERVER_ERROR status code and error message.
     @param ex The exception that occurred.
     @param request The WebRequest associated with the exception.
     @return An HTTP response with an INTERNAL_SERVER_ERROR status code and an ErrorMessage object containing an error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
        log.error("A system exception occurred: ", ex);
        String internalServerErrorMessage =
            "Sorry, we couldn't complete your request at this time. " +
            "The server encountered an error while processing your request. " +
            "Please try again later or contact support if the problem persists.";

        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                internalServerErrorMessage,
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}