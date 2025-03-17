package com.keunsori.keunsoriserver.global.exception;

public class ErrorMessage {
    // Member
    public static final String MEMBER_NOT_EXISTS_WITH_STUDENT_ID = "해당 학번을 가진 멤버가 존재하지 않습니다.";
    public static final String DUPLICATED_STUDENT_ID = "이미 존재하는 학번입니다.";
    public static final String DUPLICATED_EMAIL = "이미 존재하는 이메일입니다.";
    public static final String PASSWORD_IS_DIFFERENT_FROM_CHECK = "비밀번호와 비밀번호 확인 입력값이 다릅니다.";
    public static final String PASSWORD_INVALID_FORMAT = "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다.";
    public static final String INVALID_CURRENT_PASSWORD = "현재 비밀번호가 올바르지 않습니다.";

    // Admin Member
    public static final String INVALID_STATUS_FOR_APPROVAL = "회원이 승인 대기 상태가 아닙니다.";

    // Reservation
    public static final String RESERVATION_NOT_EXISTS_WITH_ID = "해당 아이디 값을 가진 예약이 존재하지 않습니다.";
    public static final String RESERVATION_NOT_EQUAL_MEMBER = "예약한 멤버가 아닙니다.";
    public static final String RESERVATION_ALREADY_COMPLETED = "확정된 예약은 수정/취소할 수 없습니다.";
    public static final String ANOTHER_RESERVATION_ALREADY_EXISTS = "해당 시간대에 다른 예약이 존재합니다.";
    public static final String INVALID_RESERVATION_TIME = "예약 종료 시간은 예약 시작 시간보다 나중이어야 합니다.";
    public static final String INVALID_RESERVATION_TYPE = "예약 타입이 잘못되었습니다.";
    public static final String INVALID_RESERVATION_DATE = "예약 날짜는 과거 날짜면 안됩니다.";
    public static final String INVALID_SESSION = "존재하지 않는 세션입니다.";

    // Admin Reservation
    public static final String INVALID_DATE_SCHEDULE = "설정하는 날짜가 이미 지난 날짜입니다.";
    public static final String INVALID_SCHEDULE_TIME = "시작 시간과 끝 시간의 순서가 올바르지 않습니다.";

    // Auth
    public static final String STUDENT_ID_NOT_EXISTS = "존재하지 않는 학번입니다.";
    public static final String PASSWORD_NOT_CORRECT = "비밀번호가 일치하지 않습니다.";
    public static final String INVALID_REFRESH_TOKEN = "유효하지 않은 리프레시 토큰입니다.";

    // Email
    public static final String EMAIL_NOT_EXISTS_FOR_AUTH = "인증번호를 전송하지 않았거나 인증번호가 만료되었습니다.";
    public static final String EMAIL_VERIFY_FAILED = "인증번호가 일치하지 않습니다.";
    public static final String EMAIL_VERIFY_NUMBER_GENERATION_FAILED = "인증번호 생성중 에러가 발생했습니다.";
}
