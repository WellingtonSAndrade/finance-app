package com.pagar.finance_api.core.exceptions;

public class InvalidFileTypeException extends InvalidFileException {
    public InvalidFileTypeException(String type) {
        super("Unsupported file type: " + type);
    }
}
