package com.ttech.exception;

import com.ttech.constant.ExceptionMessages;
import com.ttech.dto.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
    }

    // ResponseStatusException 
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ApiResponse.error(ex.getReason()));
    }

    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleEnumConversionError(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String value = String.valueOf(ex.getValue());
        Class<?> requiredType = ex.getRequiredType();

        StringBuilder message = new StringBuilder(
            String.format(ExceptionMessages.INVALID_PARAMETER_VALUE, value, paramName));

        if (requiredType != null && requiredType.isEnum()) {
            Object[] enumConstants = requiredType.getEnumConstants();
            String validValues = Arrays.stream(enumConstants)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            message.append(String.format(ExceptionMessages.VALID_VALUES_ARE, validValues));
        } else if (requiredType != null) {
            message.append(String.format(ExceptionMessages.EXPECTED_TYPE, requiredType.getSimpleName()));
        }

        return ResponseEntity.badRequest().body(ApiResponse.error(message.toString()));
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // Optional logging
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(String.format(ExceptionMessages.UNEXPECTED_ERROR, ex.getMessage())));
    }
}
