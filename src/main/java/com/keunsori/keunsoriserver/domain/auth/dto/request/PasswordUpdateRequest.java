package com.keunsori.keunsoriserver.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.PASSWORD_REGEX;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.PASSWORD_INVALID_FORMAT;

public record PasswordUpdateRequest(
        @NotNull(message = "비밀번호 변경을 위한 토큰값은 필수입니다.")
        String token,
        @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_INVALID_FORMAT)
        String newPassword
) {}
