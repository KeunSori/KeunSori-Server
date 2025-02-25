package com.keunsori.keunsoriserver.domain.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthNumberVerifyRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.") String email,
        @NotBlank(message = "인증번호는 필수입니다.")
        String authNumber
) {}
