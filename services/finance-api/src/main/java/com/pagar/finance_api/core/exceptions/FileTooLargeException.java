package com.pagar.finance_api.core.exceptions;

public class FileTooLargeException extends InvalidFileException {
    public FileTooLargeException(long sizeInBytes, long maxSizeInBytes) {
        super("File exceeds allowed size ("
              + formatMb(sizeInBytes) + " MB > "
              + formatMb(maxSizeInBytes) + " MB).");
    }

    private static String formatMb(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return String.format("%.2f", mb);
    }
}
