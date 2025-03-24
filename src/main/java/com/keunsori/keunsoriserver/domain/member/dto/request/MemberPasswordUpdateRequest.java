package com.keunsori.keunsoriserver.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.PASSWORD_REGEX;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.PASSWORD_INVALID_FORMAT;

public record MemberPasswordUpdateRequest(
        @NotBlank(message = "기존 비밀번호를 입력해주세요.")
        String currentPassword,
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_INVALID_FORMAT)
        String newPassword
) {
}
