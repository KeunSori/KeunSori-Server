package com.keunsori.keunsoriserver.domain.member.sign_up.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record SignUpRequest(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Pattern(regexp="[가-힣]{1,6}&,",message = "이름은 6자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "학번은 필수 입력값입니다.")
        @Pattern(regexp="^[a-zA-Z][0-9]{6}$", message="학번을 제대로 입력해주세요.")
        String studentId,

        @NotBlank(message="홍익대학교 이메일은 필수 입력값입니다.")
        @Pattern(regexp="^.{1,}@g\\.hongik\\.ac\\.kr$",message="이메일은 학생이메일입니다. ex)keunsori@g.hongik.ac.kr")
        String hongikgmail,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp="^(?=.*[!@#$%^&*(),.?\":{}|<>])[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]{8,25}$",message="비밀번호는 특수문자, 영문자, 숫자를 포함한 8자리 이상 문자열입니다.")
        String password,

        @NotBlank(message="비밀번호를 한 번 더 입력해주세요.")
        String passwordConfirm) {}
