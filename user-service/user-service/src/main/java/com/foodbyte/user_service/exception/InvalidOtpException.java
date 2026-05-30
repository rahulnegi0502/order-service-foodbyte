// InvalidOtpException.java
package com.foodbyte.user_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends BaseException {
    public InvalidOtpException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_OTP");
    }
}