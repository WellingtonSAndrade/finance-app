package com.pagar.finance_api.core.exceptions;

public class EmptyFileException extends InvalidFileException {
    public EmptyFileException() {
        super("The file is empty.");
    }
}
