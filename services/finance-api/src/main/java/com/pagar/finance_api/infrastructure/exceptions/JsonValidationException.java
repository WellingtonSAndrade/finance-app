package com.pagar.finance_api.infrastructure.exceptions;

public class JsonValidationException extends RuntimeException {
    public JsonValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
