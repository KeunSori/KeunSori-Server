package com.keunsori.keunsoriserver.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    //응답코드
    private int status;

    //해당하는 메세지
    private String message;

    public static final String MEMBER_NOT_EXISTS_WITH_STUDENT_ID = "해당 학번을 가진 멤버가 존재하지 않습니다.";
    public static final String APPROVE_COMPLETED = "이미 가입된 멤버입니다.";
}
