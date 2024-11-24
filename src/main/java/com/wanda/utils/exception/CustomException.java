package com.wanda.utils.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
public class CustomException extends RuntimeException{

    private HttpStatus statusCode;

    public CustomException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = (statusCode == null) ? HttpStatus.BAD_REQUEST : statusCode;
    }
}
