package com.keunsori.keunsoriserver.domain.auth.service;

import com.keunsori.keunsoriserver.domain.auth.repository.RefreshTokenRepository;
import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordFindRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import com.keunsori.keunsoriserver.global.util.CookieUtil;
import com.keunsori.keunsoriserver.global.util.TokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import com.keunsori.keunsoriserver.global.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.keunsori.keunsoriserver.global.constant.UrlConstant.PASSWORD_CHANGE_LINK;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_NOT_EXISTS;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final TokenUtil tokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final EmailUtil emailUtil;

    public void login(String studentId, HttpServletResponse response) {
        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new MemberException(STUDENT_ID_NOT_EXISTS));

        String accessToken = tokenUtil.generateAccessToken(member.getStudentId(), member.getName(), member.getStatus());
        String refreshToken = tokenUtil.generateRefreshToken(member.getStudentId(), member.getName(), member.getStatus());

        refreshTokenRepository.saveRefreshToken(member.getStudentId(), refreshToken, JwtProperties.REFRESH_TOKEN_VALIDITY_TIME);

        CookieUtil.addAccessTokenCookie(response, accessToken);
        CookieUtil.addRefreshTokenCookie(response, refreshToken);
    }

    public void logout(String refreshToken, HttpServletResponse response){
        if(refreshToken != null){
            String studentId = tokenUtil.getStudentIdFromToken(refreshToken);
            refreshTokenRepository.deleteRefreshToken(studentId);
        }

        CookieUtil.deleteCookie(response, "Access-Token");
        CookieUtil.deleteCookie(response, "Refresh-Token");
    }

    public void findPassword(PasswordFindRequest request) {
        Member member = memberRepository.findByStudentIdIgnoreCase(request.studentId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        member.validateEmail(request.email());

        String encryptedEmail = ""; // TODO : email 암호화

        String passwordChangeLink = PASSWORD_CHANGE_LINK + "?key=" + encryptedEmail;

        emailUtil.sendPasswordInitializeLink(request.email(), passwordChangeLink);

        log.info("[AuthService] 비밀번호 초기화: studentId: {}", request.studentId());
    }
}
