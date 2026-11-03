package com.example.ArithmeticQuiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
                // Print error in Spring Boot console
                logger.error("ERROR: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
                // Print unexpected errors in Spring Boot console
                logger.error("UNEXPECTED ERROR: ", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Map.of("error", "Internal server error"));
        }
    
        @ExceptionHandler(ResponseStatusException.class) 
        public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
                // Print response status errors in Spring Boot console
                logger.error("HTTP ERROR: {}", ex.getReason());
                return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
                // Print validation errors in Spring Boot console
                List<String> details = ex.getBindingResult().getAllErrors().stream()
                        .map(error -> (error instanceof FieldError field ? field.getField() : error.getObjectName())
                         + ": " + error.getDefaultMessage()).toList();
                logger.error("VALIDATION ERROR: {}", details);
                return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "details", details));
        }       

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
                // Print malformed request body errors in Spring Boot console
                logger.error("MALFORMED REQUEST BODY: {}", ex.getMessage());
                String message = "Malformed request body";
                if (ex.getMostSpecificCause() instanceof InvalidFormatException ife) {
                        message = "Invalid value '" + ife.getValue() + "'";
                        if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                                message += ", allowed values: " + Arrays.toString(ife.getTargetType().getEnumConstants());
                        }
                }
                return ResponseEntity.badRequest().body(Map.of("error", message));
        }
}
