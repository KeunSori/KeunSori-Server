package com.keunsori.keunsoriserver.domain.auth.exception;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException(String message ) {
        super(message);
    }
}
