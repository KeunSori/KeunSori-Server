package com.keunsori.keunsoriserver.auth.service;

import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordUpdateLinkSendRequest;
import com.keunsori.keunsoriserver.domain.auth.service.AuthService;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.properties.UrlProperties;
import com.keunsori.keunsoriserver.global.util.EmailUtil;
import com.keunsori.keunsoriserver.global.util.TokenUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.MEMBER_NOT_EXISTS_WITH_STUDENT_ID;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_DOES_NOT_MATCH_WITH_EMAIL;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private TokenUtil tokenUtil;

    @Mock
    private UrlProperties urlProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void 비밀번호_변경_링크_전송_성공() {
        // given
        Member member = Member.builder()
                .studentId("C011013")
                .password("123456")
                .email("test@gmail.com")
                .build();

        PasswordUpdateLinkSendRequest request = new PasswordUpdateLinkSendRequest(
                "C011013",
                "test@gmail.com"
        );

        given(memberRepository.findByStudentIdIgnoreCase("C011013")).willReturn(Optional.of(member));
        given(tokenUtil.generatePasswordUpdateToken("C011013")).willReturn("PW_CHANGE_TOKEN");
        given(urlProperties.getPasswordChangePath()).willReturn("password/change");

        // when
        authService.sendPasswordUpdateLink(request);

        // then
        verify(emailUtil, times(1)).sendPasswordInitializeLink(anyString(), anyString());
    }

    @Test
    void 비밀번호_변경_링크_전송_실패_미존재_학번() {
        // given
        PasswordUpdateLinkSendRequest request = new PasswordUpdateLinkSendRequest(
                "C011013",
                "test@gmail.com"
        );

        given(memberRepository.findByStudentIdIgnoreCase("C011013")).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> authService.sendPasswordUpdateLink(request))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_EXISTS_WITH_STUDENT_ID);
    }

    @Test
    void 비밀번호_변경_링크_전송_실패_학번_이메일_불일치() {
        // given
        Member member = Member.builder()
                .studentId("C011013")
                .password("123456")
                .email("test@gmail.com")
                .build();

        PasswordUpdateLinkSendRequest request = new PasswordUpdateLinkSendRequest(
                "C011013",
                "test2@gmail.com"
        );

        given(memberRepository.findByStudentIdIgnoreCase("C011013")).willReturn(Optional.of(member));

        // when & then
        Assertions.assertThatThrownBy(() -> authService.sendPasswordUpdateLink(request))
                .isInstanceOf(MemberException.class)
                .hasMessage(STUDENT_ID_DOES_NOT_MATCH_WITH_EMAIL);
    }
}
