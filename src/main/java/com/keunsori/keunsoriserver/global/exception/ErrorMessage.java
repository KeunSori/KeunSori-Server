package com.keunsori.keunsoriserver.global.exception;

public class ErrorMessage {
    // Member
    public static final String MEMBER_NOT_EXISTS_WITH_STUDENT_ID = "해당 학번을 가진 멤버가 존재하지 않습니다.";
    public static final String DUPLICATED_STUDENT_ID = "이미 존재하는 학번입니다.";
    public static final String DUPLICATED_EMAIL = "이미 존재하는 이메일입니다.";
    public static final String PASSWORD_IS_DIFFERENT_FROM_CHECK = "비밀번호와 비밀번호 확인 입력값이 다릅니다.";
    public static final String PASSWORD_INVALID_FORMAT = "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다.";
    public static final String STUDENT_ID_INVALID_FORMAT = "학번 형식이 올바르지 않습니다.";
    public static final String EMAIL_INVALID_FORMAT = "이메일 형식이 올바르지 않습니다.";
    public static final String INVALID_CURRENT_PASSWORD = "현재 비밀번호가 올바르지 않습니다.";
    public static final String STUDENT_ID_DOES_NOT_MATCH_WITH_EMAIL = "가입한 학번과 이메일이 일치하지 않습니다";
    public static final String PASSWORD_SAME_AS_OLD = "새 비밀번호가 기존 비밀번호와 동일합니다.";
    public static final String MEMBER_CANNOT_BE_DELETED_BECAUSE_OF_REGULAR_RESERVATIONS = "정기 예약 팀장인 상태에서는 회원 탈퇴가 불가능합니다.";
    public static final String MEMBER_IS_NOT_ADMIN = "관리자 계정이 아닙니다.";

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
    public static final String RESERVATION_NOT_EQUALS_TEAM_LEADER = "정기 예약 삭제는 팀장만 가능합니다.";
    public static final String RESERVATION_OUT_OF_SCHEDULE = "예약 가능한 시간이 아닙니다.";

    // Admin Reservation
    public static final String INVALID_DATE_SCHEDULE = "설정하는 날짜가 이미 지난 날짜입니다.";
    public static final String INVALID_SCHEDULE_TIME = "시작 시간과 끝 시간의 순서가 올바르지 않습니다.";
    public static final String PARTIAL_RESERVATION_NOT_FOUND = "삭제하려는 일부 예약이 존재하지 않습니다.";
    public static final String EMPTY_MANAGEMENT_REQUEST = "주간 스케줄, 정기 예약 생성, 삭제 중 최소 하나는 존재해야 합니다.";

    // Regular Reservation
    public static final String REGULAR_RESERVATION_NOT_DELETABLE = "정기 예약은 관리자 또는 예약 팀장만 삭제할 수 있습니다.";
    public static final String INVALID_REGULAR_RESERVATION_TIME = "정기 예약 종료 시간은 예약 시작 시간보다 나중이어야 합니다.";
    public static final String INVALID_REGULAR_RESERVATION_DATE = "정기 예약 날짜는 과거 날짜면 안됩니다.";
    public static final String ANOTHER_REGULAR_RESERVATION_ALREADY_EXISTS = "해당 시간에 이미 다른 정기 예약이 존재합니다.";
    public static final String PARTIAL_REGULAR_RESERVATION_MISSING = "일부 정기 예약이 존재하지 않습니다.";
    public static final String APPLY_DATE_SAME_WITH_END_DATE = "정기예약의 시작일과 종료일을 같은 날짜로 설정할 수 없습니다.";
    public static final String REGULAR_RESERVATION_ACCESS_DENIED = "정기 예약 전체 조회는 관리자만 가능합니다.";
    public static final String DAY_NOT_IN_APPLY_PERIOD = "시간표 적용 기간에 해당 요일이 포함되지 않아 정기예약을 생성하지 못합니다.";


    // Auth
    public static final String STUDENT_ID_NOT_EXISTS = "존재하지 않는 학번입니다.";
    public static final String INVALID_PASSWORD = "비밀번호가 일치하지 않습니다.";
    public static final String INVALID_REFRESH_TOKEN = "유효하지 않은 Refresh-token 입니다.";
    public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
    public static final String EXPIRED_TOKEN = "만료된 토큰입니다.";

    // Email
    public static final String EMAIL_NOT_EXISTS_FOR_AUTH = "인증번호를 전송하지 않았거나 인증번호가 만료되었습니다.";
    public static final String EMAIL_VERIFY_FAILED = "인증번호가 일치하지 않습니다.";
    public static final String EMAIL_VERIFY_NUMBER_GENERATION_FAILED = "인증번호 생성중 에러가 발생했습니다.";

    // Random
    public static final String INITIALIZED_PASSWORD_GENERATION_FAILED = "임시 비밀번호 생성에 실패하였습니다.";
}
