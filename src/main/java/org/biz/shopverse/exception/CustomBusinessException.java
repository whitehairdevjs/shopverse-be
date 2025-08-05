package org.biz.shopverse.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomBusinessException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    
    public CustomBusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    
    public CustomBusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    
    public CustomBusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public CustomBusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = httpStatus;
    }
} 