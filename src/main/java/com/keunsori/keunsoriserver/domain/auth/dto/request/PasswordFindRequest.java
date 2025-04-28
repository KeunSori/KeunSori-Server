package com.keunsori.keunsoriserver.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.STUDENT_ID_REGEX;

public record PasswordFindRequest(
        @Pattern(regexp = STUDENT_ID_REGEX, message = "학번 형식이 올바르지 않습니다.")
        String studentId,
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email
) {}
