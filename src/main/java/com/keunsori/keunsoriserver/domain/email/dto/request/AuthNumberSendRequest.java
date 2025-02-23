package com.keunsori.keunsoriserver.domain.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthNumberSendRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 주소 형식이 올바르지 않습니다.")
        String email
) {}
