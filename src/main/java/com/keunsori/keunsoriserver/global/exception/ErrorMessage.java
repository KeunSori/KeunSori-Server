package com.keunsori.keunsoriserver.global.exception;

public class ErrorMessage {
    // Member
    public static final String MEMBER_NOT_EXISTS_WITH_STUDENT_ID = "해당 학번을 가진 멤버가 존재하지 않습니다.";

    // Admin Member
    public static final String INVALID_STATUS_FOR_APPROVAL = "회원이 승인 대기 상태가 아닙니다.";

    // Reservation
    public static final String RESERVATION_NOT_EXISTS_WITH_ID = "해당 아이디 값을 가진 예약이 존재하지 않습니다.";
    public static final String RESERVATION_NOT_EQUAL_MEMBER = "예약한 멤버가 아닙니다.";
    public static final String RESERVATION_ALREADY_COMPLETED = "확정된 예약은 수정/취소할 수 없습니다.";
    public static final String ANOTHER_RESERVATION_EXISTS = "해당 시간대에 다른 예약이 존재합니다.";
    public static final String INVALID_RESERVATION_TIME = "예약 종료 시간은 예약 시작 시간보다 나중이어야 합니다.";

    // Admin Schedule
    public static final String INVALID_DATE_SCHEDULE = "설정하는 날짜가 이미 지난 날짜입니다.";
    public static final String INVALID_SCHEDULE_TIME = "시작 시간과 끝 시간의 순서가 올바르지 않습니다.";
}
