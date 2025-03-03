package com.keunsori.keunsoriserver.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.PASSWORD_REGEX;

public record MemberPasswordUpdateRequest(
        @NotBlank(message = "기존 비밀번호를 입력해주세요.")
        String currentPassword,
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        String newPassword,
        @NotBlank(message = "새 비밀번호를 확인을 입력해주세요.")
        String passwordConfirm
) {
}
