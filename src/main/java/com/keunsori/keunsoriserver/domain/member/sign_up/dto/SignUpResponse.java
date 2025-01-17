package com.keunsori.keunsoriserver.domain.member.sign_up.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {
    private String name;
    private String studentId;
    private String hongikgmail;
}
