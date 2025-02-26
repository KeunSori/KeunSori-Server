package com.keunsori.keunsoriserver.domain.auth.login;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.PASSWORD_NOT_CORRECT;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_NOT_EXISTS;

import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.login.dto.response.LoginResponse;
import com.keunsori.keunsoriserver.domain.auth.redis.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.auth.service.TokenService;
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
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        //학번으로 사용자 조회
        Member member= memberRepository.findByStudentIdIgnoreCase(loginRequest.studentId())
                .orElseThrow(() -> new MemberException(STUDENT_ID_NOT_EXISTS));

        //비밀번호 일치하는지 검증
        if(!passwordEncoder.matches(loginRequest.password(), member.getPassword())){
            throw new AuthException(PASSWORD_NOT_CORRECT);
        }

        //Access Token, Refresh Token 생성
        String accessToken= tokenService.generateAccessToken(
                member.getStudentId(), member.getName(), member.getStatus()
        );

        String refreshToken= tokenService.generateRefreshToken(
                member.getStudentId(), member.getName(), member.getStatus()
        );

        //Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(member.getStudentId(), refreshToken, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);

        return new LoginResponse(
                accessToken,
                refreshToken,
                String.valueOf(tokenService.getExpirationTime(accessToken)),
                member.getName(),
                member.getStatus()
        );
    }
}
