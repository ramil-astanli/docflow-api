package com.ramilastanli.docflow.config;

public final class ApplicationConstants {

    private ApplicationConstants() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "dGhpcy1pcy1hLXZlcnktc2VjdXJlLWtleS1mb3ItZG9jdW1lbnQtZmxvdy1hcGktMjAyNg==";
    public static final String JWT_HEADER = "Authorization";

    public static final String SYSTEM = "SYSTEM";
    public static final String DOCUMENT_ID_HEADER = "documentId"; // Integration mesajları üçün
}