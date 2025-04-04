package com.keunsori.keunsoriserver.global.exception;

import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {
    private ErrorCode errorCode;
    public ReservationException(ErrorCode errorCode) {
        super(errorCode.getMassage());
        this.errorCode = errorCode;
    }
}
