package com.keunsori.keunsoriserver.domain.auth.service;

import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordInitializeRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.EmailException;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.exception.RandomException;
import com.keunsori.keunsoriserver.global.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
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

        String initializedPassword = generateRandomPassword();
        member.updatePassword(passwordEncoder.encode(initializedPassword));

        emailUtil.sendPasswordInitializeEmail(request.email(), initializedPassword);
    }

    private String generateRandomPassword() {
        try {
            byte[] randomBytes = new byte[16];
            SecureRandom random = SecureRandom.getInstanceStrong();

            random.nextBytes(randomBytes);
            return Base64.getUrlEncoder().encodeToString(randomBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RandomException(INITIALIZED_PASSWORD_GENERATION_FAILED);
        }
    }
}
