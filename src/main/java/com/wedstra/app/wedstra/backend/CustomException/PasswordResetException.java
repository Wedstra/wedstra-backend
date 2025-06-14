package com.wedstra.app.wedstra.backend.CustomException;

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
    }
}

