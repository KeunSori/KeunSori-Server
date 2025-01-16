package com.keunsori.keunsoriserver.domain.auth.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequestDTO {
    String studentId;
    String password;
}
