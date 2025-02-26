package com.keunsori.keunsoriserver.domain.auth.service;

import com.keunsori.keunsoriserver.domain.auth.redis.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import com.keunsori.keunsoriserver.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    @Transactional
    public void login(Member member, HttpServletResponse response){

        String accessToken=tokenService.generateAccessToken(member.getStudentId(), member.getName(),member.getStatus());
        String refreshToken=tokenService.generateRefreshToken(member.getStudentId(), member.getName(),member.getStatus());

        refreshTokenService.saveRefreshToken(member.getStudentId(),refreshToken, JwtProperties.REFRESH_TOKEN_VALIDITY_TIME);

        CookieUtil.addAccessTokenCookie(response, accessToken);
        CookieUtil.addRefreshTokenCookie(response, refreshToken);

    }

    @Transactional
    public void reissueToken(String refreshToken, HttpServletResponse response){

        if(refreshToken == null){
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }

        String studentId = tokenService.getStudentIdFromToken(refreshToken);
        String storedRefreshToken = refreshTokenService.getRefreshToken(studentId);

        if(storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findByStudentIdIgnoreCase(studentId)
                .orElseThrow(() -> new MemberException(STUDENT_ID_NOT_EXISTS));

        String newAccessToken = tokenService.generateAccessToken(member.getStudentId(), member.getName(), member.getStatus());
        CookieUtil.addAccessTokenCookie(response, newAccessToken);

    }

    @Transactional
    public void logout(String refreshToken, HttpServletResponse response){

        if(refreshToken != null){
            String studentId = tokenService.getStudentIdFromToken(refreshToken);
            refreshTokenService.deleteRefreshToken(studentId);
        }

        CookieUtil.deleteCookie(response, "Acccess-Token");
        CookieUtil.deleteCookie(response, "Refresh-Token");
    }

}
