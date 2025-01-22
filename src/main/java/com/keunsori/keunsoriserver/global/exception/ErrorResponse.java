package com.keunsori.keunsoriserver.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    //응답코드
    private int status;

    //해당하는 메세지
    private String message;
}
