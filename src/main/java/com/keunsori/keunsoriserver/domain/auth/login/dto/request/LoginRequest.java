package com.keunsori.keunsoriserver.domain.auth.login.dto.request;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.PASSWORD_REGEX;
import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.STUDENT_ID_REGEX;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(

        @NotBlank(message = "아이디를 입력해주세요.")
        @Pattern(
                regexp = STUDENT_ID_REGEX,
                message = "학번 형식이 올바르지 않습니다."
        )
        String studentId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = PASSWORD_REGEX,
                message = "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다."
        )
        String password
) {}
