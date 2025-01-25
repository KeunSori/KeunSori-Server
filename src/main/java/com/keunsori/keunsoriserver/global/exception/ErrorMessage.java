package com.keunsori.keunsoriserver.global.exception;

public class ErrorMessage {
    public static final String MEMBER_NOT_EXISTS_WITH_STUDENT_ID = "해당 학번을 가진 멤버가 존재하지 않습니다.";
    public static final String RESERVATION_NOT_EXISTS_WITH_ID = "해당 아이디 값을 가진 예약이 존재하지 않습니다.";
    public static final String RESERVATION_NOT_EQUAL_MEMBER = "예약한 멤버가 아닙니다.";
    public static final String RESERVATION_COMPLETED = "이미 확정된 예약입니다.";
    public static final String INVALID_STATUS_FOR_APPROVAL = "회원이 승인 대기 상태가 아닙니다.";
}
