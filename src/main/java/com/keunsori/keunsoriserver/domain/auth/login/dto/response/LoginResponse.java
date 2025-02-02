package com.keunsori.keunsoriserver.domain.auth.login.dto.response;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;


public record LoginResponse(String accessToken, String refreshToken, String accessTokenExpireTime, String name, MemberStatus memberStatus) {}
