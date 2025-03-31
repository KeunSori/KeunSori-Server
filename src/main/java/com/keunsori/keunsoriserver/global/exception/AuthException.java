package com.keunsori.keunsoriserver.global.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private ErrorCode errorCode;
    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMassage());
        this.errorCode = errorCode;
    }
}
