package com.keunsori.keunsoriserver.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    //응답코드
    private int status;

    //해당 메세지
    private String message;
}
