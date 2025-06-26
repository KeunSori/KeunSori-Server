package com.keunsori.keunsoriserver.domain.auth.service;

import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.auth.repository.RefreshTokenRepository;
import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordUpdateLinkSendRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public String login(String studentId, HttpServletResponse response) {
        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new MemberException(STUDENT_ID_NOT_EXISTS));

        String accessToken = tokenUtil.generateAccessToken(member.getStudentId(), member.getName(), member.getStatus());
        String refreshToken = tokenUtil.generateRefreshToken(member.getStudentId(), member.getName(), member.getStatus());

        refreshTokenRepository.saveRefreshToken(member.getStudentId(), refreshToken, JwtProperties.REFRESH_TOKEN_VALIDITY_TIME);

        CookieUtil.addAccessTokenCookie(response, accessToken);
        CookieUtil.addRefreshTokenCookie(response, refreshToken);

        //스웨거 API 테스트 통과용 Access Token 반환
        return accessToken;
    }

    public void logout(String refreshToken, HttpServletResponse response){
        if(refreshToken != null){
            String studentId = tokenUtil.getStudentIdFromToken(refreshToken);
            refreshTokenRepository.deleteRefreshToken(studentId);
        }

        CookieUtil.deleteCookie(response, "Access-Token");
        CookieUtil.deleteCookie(response, "Refresh-Token");
    }

    public void sendPasswordUpdateLink(PasswordUpdateLinkSendRequest request) {
        Member member = memberRepository.findByStudentIdIgnoreCase(request.studentId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        member.validateEmail(request.email());

        String token = tokenUtil.generatePasswordUpdateToken(member.getStudentId());
        String passwordChangeLink = PASSWORD_CHANGE_LINK + "?key=" + token;
        emailUtil.sendPasswordInitializeLink(request.email(), passwordChangeLink);

        log.info("[AuthService] 비밀번호 재설정 이메일 전송ㅇ: studentId: {}", request.studentId());
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        tokenUtil.validateToken(request.token());
        String studentId = tokenUtil.getStudentIdFromToken(request.token());

        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        String encodedPassword = passwordEncoder.encode(request.newPassword());
        member.updatePassword(encodedPassword);

        log.info("[AuthService] 링크를 통한 비밀번호 변경: studentId: {}", studentId);
    }
}
