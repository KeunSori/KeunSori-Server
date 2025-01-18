package com.keunsori.keunsoriserver.domain.auth.login.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(

        @NotBlank(message = "아이디를 입력해주세요.")
        String studentId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>])[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]{8,25}$",
                message = "비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다."
        )
        String password
) {}
