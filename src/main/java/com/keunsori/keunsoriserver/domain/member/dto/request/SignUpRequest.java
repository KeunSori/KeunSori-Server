package com.keunsori.keunsoriserver.domain.member.dto.request;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.PASSWORD_REGEX;
import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.STUDENT_ID_REGEX;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.PASSWORD_INVALID_FORMAT;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record SignUpRequest(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Pattern(regexp = "[가-힣]{1,6}$", message = "이름은 한글 6자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "학번은 필수 입력값입니다.")
        @Pattern(regexp = STUDENT_ID_REGEX, message = "학번을 제대로 입력해주세요.")
        String studentId,

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_INVALID_FORMAT)
        String password,

        @NotBlank(message = "비밀번호를 한 번 더 입력해주세요.")
        String passwordConfirm) {}
