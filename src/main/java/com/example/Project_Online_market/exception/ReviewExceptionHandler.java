package com.example.Project_Online_market.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.Project_Online_market.response.ApiResponses;

/**
 * Global exception handler to catch and handle all exceptions across the application.
 * It intercepts validation errors and runtime exceptions, returning a structured JSON error response.
 */
@RestControllerAdvice
public class ReviewExceptionHandler {

    /**
     * Handles validation errors thrown when method arguments fail validation.
     *
     * @param ex the MethodArgumentNotValidException thrown by Spring validation
     * @return ResponseEntity with a JSON error message and a 400 status code
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponses> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                                .collect(Collectors.toList());
        String errorMessage = String.join(", ", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ApiResponses.error(errorMessage, 400));
    }

    /**
     * Handles all runtime exceptions not explicitly handled elsewhere.
     *
     * @param ex the RuntimeException thrown by the application
     * @return ResponseEntity with a JSON error message and a 500 status code
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponses> handleRuntimeExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponses.error(ex.getMessage(), 500));
    }
}
