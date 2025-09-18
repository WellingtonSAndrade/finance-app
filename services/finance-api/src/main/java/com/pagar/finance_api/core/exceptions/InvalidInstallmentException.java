package com.pagar.finance_api.core.exceptions;

public class InvalidInstallmentException extends RuntimeException {
    public InvalidInstallmentException(String message) {
        super(message);
    }
}
