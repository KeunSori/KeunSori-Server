package com.keunsori.keunsoriserver.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.PASSWORD_REGEX;

public record MemberPasswordUpdateRequest(
        @NotBlank(message = "기존 비밀번호를 입력해주세요.")
        @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다.")
        String currentPassword,
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다.")
        String newPassword,
        @NotBlank(message = "새 비밀번호를 확인을 입력해주세요.")
        String passwordConfirm
) {
}
