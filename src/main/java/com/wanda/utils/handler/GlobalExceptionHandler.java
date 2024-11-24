package com.wanda.utils.handler;

import com.wanda.utils.exception.CustomException;
import com.wanda.utils.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleResourceNotFoundException(CustomException ex, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(
                false,
               ex.getMessage()
        );

        System.out.println("hadnled by global exception custom");

        return new ResponseEntity<>(errorDetails, ex.getStatusCode());
    }

    // Handle global exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(
                false,
                ex.getMessage()
        );

        System.out.println("hadnled by global exception");
        ex.printStackTrace();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
