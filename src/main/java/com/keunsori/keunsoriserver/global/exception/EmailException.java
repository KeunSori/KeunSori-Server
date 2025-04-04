package com.keunsori.keunsoriserver.global.exception;

import lombok.Getter;

@Getter
public class EmailException extends RuntimeException {
    private ErrorCode errorCode;
    public EmailException(ErrorCode errorCode) {
        super(errorCode.getMassage());
        this.errorCode = errorCode;
    }
}
