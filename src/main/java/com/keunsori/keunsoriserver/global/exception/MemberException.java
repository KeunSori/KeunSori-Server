package com.keunsori.keunsoriserver.global.exception;

import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
    private ErrorCode errorCode;

    public MemberException(ErrorCode errorCode) {
        super(errorCode.getMassage());
        this.errorCode = errorCode;
    }
}
