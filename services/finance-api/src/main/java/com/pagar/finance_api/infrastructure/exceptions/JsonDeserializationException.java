package com.pagar.finance_api.infrastructure.exceptions;

public class JsonDeserializationException extends RuntimeException {
    public JsonDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
