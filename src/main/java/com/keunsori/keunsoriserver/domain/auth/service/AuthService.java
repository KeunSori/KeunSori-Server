package com.keunsori.keunsoriserver.domain.auth.service;

import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordInitializeRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.util.EmailUtil;
import com.keunsori.keunsoriserver.global.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;

    @Transactional
    public void initializePassword(PasswordInitializeRequest request) {
        Member member = memberRepository.findByStudentIdIgnoreCase(request.studentId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        if (!member.hasEmail(request.email())) {
            throw new MemberException(STUDENT_ID_DOES_NOT_MATCH_WITH_EMAIL);
        }

        String initializedPassword = RandomUtil.generateRandomPassword();
        member.updatePassword(passwordEncoder.encode(initializedPassword));

        emailUtil.sendPasswordInitializeEmail(request.email(), initializedPassword);

        log.info("[AuthService] 비밀번호 초기화: studentId: {}", request.studentId());
    }
}
