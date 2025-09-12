package com.pagar.finance_api.core.exceptions;

public class UserMismatchException extends RuntimeException {
    public UserMismatchException(String message) {
        super(message);
    }
}
