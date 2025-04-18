package com.eventmanagement.event_ai.exception;

public class GeminiAPIException extends RuntimeException {
    public GeminiAPIException(String message) {
        super(message);
    }

    public GeminiAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}