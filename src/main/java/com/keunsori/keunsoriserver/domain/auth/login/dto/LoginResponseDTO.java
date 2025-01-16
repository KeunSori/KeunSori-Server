package com.keunsori.keunsoriserver.domain.auth.login.dto;

import com.keunsori.keunsoriserver.domain.member.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {

    private String studentId;
    private String name;
    private MemberStatus status;
    private String accessToken;
    private String refreshToken;
    private String tokenExpireTime;
}
