package com.keunsori.keunsoriserver.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.PASSWORD_REGEX;

public record PasswordUpdateRequest(
        @NotNull(message = "비밀번호 변경을 위한 토큰값은 필수입니다.")
        String token,
        @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다.")
        String newPassword
) {}
