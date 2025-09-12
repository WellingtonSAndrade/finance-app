package com.pagar.finance_api.core.exceptions;

public class OcrProcessingException extends RuntimeException {
    public OcrProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
