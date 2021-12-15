package com.recipebook.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author - AvanishKishorPandey
 */

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class APIException extends RuntimeException {
    public APIException(String message) {
        super(message);
    }
}