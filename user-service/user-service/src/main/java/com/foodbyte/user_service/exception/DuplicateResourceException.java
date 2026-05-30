// DuplicateResourceException.java
package com.foodbyte.user_service.exception;

import com.foodbyte.user_service.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}