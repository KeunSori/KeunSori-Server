package com.keunsori.keunsoriserver.domain.auth.login;

import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.login.dto.response.LoginResponse;
import com.keunsori.keunsoriserver.domain.auth.redis.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        //학번으로 사용자 조회
        Member member= memberRepository.findByStudentId(loginRequest.studentId())
                .orElseThrow(() -> new MemberException("존재하지 않는 학번입니다."));

        //비밀번호 일치하는지 검증
        if(!passwordEncoder.matches(loginRequest.password(), member.getPassword())){
            throw new AuthException("비밀번호가 일치하지 않습니다.");
        }

        //Access Token, Refresh Token 생성
        String accessToken= jwtTokenManager.generateAccessToken(
                member.getStudentId(), member.getName(), member.getStatus()
        );

        String refreshToken= jwtTokenManager.generateRefreshToken(
                member.getStudentId(), member.getName(), member.getStatus()
        );

        //Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(member.getStudentId(), refreshToken, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);

        return new LoginResponse(
                accessToken,
                refreshToken,
                String.valueOf(jwtTokenManager.getExpirationTime(accessToken)),
                member.getName(),
                member.getStatus()
        );
    }
}
