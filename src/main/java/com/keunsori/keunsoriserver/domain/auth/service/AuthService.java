package com.keunsori.keunsoriserver.domain.auth.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import com.keunsori.keunsoriserver.global.util.CookieUtil;
import com.keunsori.keunsoriserver.global.util.TokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REFRESH_TOKEN;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenUtil tokenUtil;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    @Transactional
    public void login(String studentId, HttpServletResponse response){
        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new MemberException(STUDENT_ID_NOT_EXISTS));

        String accessToken= tokenUtil.generateAccessToken(member.getStudentId(), member.getName(),member.getStatus());
        String refreshToken= tokenUtil.generateRefreshToken(member.getStudentId(), member.getName(),member.getStatus());

        refreshTokenService.saveRefreshToken(member.getStudentId(),refreshToken, JwtProperties.REFRESH_TOKEN_VALIDITY_TIME);

        CookieUtil.addAccessTokenCookie(response, accessToken);
        CookieUtil.addRefreshTokenCookie(response, refreshToken);

    }


    @Transactional
    public void logout(String refreshToken, HttpServletResponse response){

        if(refreshToken != null){
            String studentId = tokenUtil.getStudentIdFromToken(refreshToken);
            refreshTokenService.deleteRefreshToken(studentId);
        }

        CookieUtil.deleteCookie(response, "Acccess-Token");
        CookieUtil.deleteCookie(response, "Refresh-Token");
    }
}
