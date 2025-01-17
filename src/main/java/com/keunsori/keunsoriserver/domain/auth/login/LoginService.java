package com.keunsori.keunsoriserver.domain.auth.login;

import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginResponse;
import com.keunsori.keunsoriserver.domain.auth.redis.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.member.Member;
import com.keunsori.keunsoriserver.domain.member.MemberRepository;
import com.keunsori.keunsoriserver.domain.member.MemberStatus;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.exception.MemberNotFoundException;
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

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) throws MemberNotFoundException, AuthException.InvalidPasswordException {

        //학번으로 사용자 조회
        Member member= memberRepository.findByStudentId(loginRequest.getStudentId())
                .orElseThrow(()->new MemberNotFoundException("존재하지 않는 학번입니다."));

        //비밀번호 일치하는지 검증
        if(!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())){
            throw new AuthException.InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        //Access Token, Refresh Token 생성
        String accessToken= jwtTokenManager.generateAccessToken(
                member.getStudentId(), member.getName(), member.getStatus()
        );

        String refreshToken= jwtTokenManager.generateRefreshToken(
                member.getStudentId(), member.getName(), member.getStatus()
        );

        //Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(member.getStudentId(), refreshToken, 7*24*60*60*1000L);

        return new LoginResponse(member.getStudentId(),member.getName(),
                MemberStatus.일반,
                accessToken,
                refreshToken,
                String.valueOf(jwtTokenManager.getExpirationTime(accessToken)));

    }

}
