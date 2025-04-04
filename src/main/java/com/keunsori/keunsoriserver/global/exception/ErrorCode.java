package com.keunsori.keunsoriserver.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(400, "COMMON-001", "유효성 검증에 실패했습니다."),
    INTERNAL_SERVER_ERROR(500,"COMMON-002", "서버에서 처리할 수 없습니다."),

    // Member
    MEMBER_NOT_EXISTS_WITH_STUDENT_ID(404, "MEMBER-001", "해당 학번을 가진 멤버가 존재하지 않습니다."),
    DUPLICATED_STUDENT_ID(409, "MEMBER-002", "이미 존재하는 학번입니다."),
    DUPLICATED_EMAIL(409, "MEMBER-003", "이미 존재하는 이메일입니다."),
    PASSWORD_IS_DIFFERENT_FROM_CHECK(400, "MEMBER-004", "비밀번호와 비밀번호 확인 입력값이 다릅니다."),
    PASSWORD_INVALID_FORMAT(400, "MEMBER-005", "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다."),
    INVALID_CURRENT_PASSWORD(400, "MEMBER-006", "현재 비밀번호가 올바르지 않습니다."),
    PASSWORD_SAME_AS_OLD(400, "MEMBER-007", "새 비밀번호가 기존 비밀번호와 동일합니다."),

    // Admin Member
    INVALID_STATUS_FOR_APPROVAL(403, "ADMIN-MEMBER-001", "회원이 승인 대기 상태가 아닙니다."),

    // Reservation
    RESERVATION_NOT_EXISTS_WITH_ID(404, "RESERVATION-001", "해당 아이디 값을 가진 예약이 존재하지 않습니다."),
    RESERVATION_NOT_EQUAL_MEMBER(403, "RESERVATION-002", "예약한 멤버가 아닙니다."),
    RESERVATION_ALREADY_COMPLETED(400, "RESERVATION-003", "확정된 예약은 수정/취소할 수 없습니다."),
    ANOTHER_RESERVATION_ALREADY_EXISTS(409, "RESERVATION-004", "해당 시간대에 다른 예약이 존재합니다."),
    INVALID_RESERVATION_TIME(400, "RESERVATION-005", "예약 종료 시간은 예약 시작 시간보다 나중이어야 합니다."),
    INVALID_RESERVATION_TYPE(400, "RESERVATION-006", "예약 타입이 잘못되었습니다."),
    INVALID_RESERVATION_DATE(400, "RESERVATION-007", "예약 날짜는 과거 날짜면 안됩니다."),
    INVALID_SESSION(404, "RESERVATION-008", "존재하지 않는 세션입니다."),

    // Admin Reservation
    INVALID_DATE_SCHEDULE(400, "ADMIN-RESERVATION-001", "설정하는 날짜가 이미 지난 날짜입니다."),
    INVALID_SCHEDULE_TIME(400, "ADMIN-RESERVATION-002", "시작 시간과 끝 시간의 순서가 올바르지 않습니다."),

    // Auth
    STUDENT_ID_NOT_EXISTS(404, "AUTH-001", "존재하지 않는 학번입니다."),
    PASSWORD_NOT_CORRECT(401, "AUTH-002", "비밀번호가 일치하지 않습니다.") ,
    INVALID_REFRESH_TOKEN(401, "AUTH-003", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_TOKEN(401, "AUTH-004", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "AUTH-005", "만료된 토큰입니다."),

    // Email
    EMAIL_NOT_EXISTS_FOR_AUTH(400, "EMAIL-001", "인증번호를 전송하지 않았거나 인증번호가 만료되었습니다."),
    EMAIL_VERIFY_FAILED(400, "EMAIL-002", "인증번호가 일치하지 않습니다."),
    EMAIL_VERIFY_NUMBER_GENERATION_FAILED(500, "EMAIL-003", "인증번호 생성중 에러가 발생했습니다.");


    private final int status;
    private final String code;
    private final String massage;

    ErrorCode(int status, String code, String massage) {
        this.status = status;
        this.code = code;
        this.massage = massage;
    }
}
