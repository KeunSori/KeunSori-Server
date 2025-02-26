package com.keunsori.keunsoriserver.domain.member.service;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.DUPLICATED_EMAIL;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.DUPLICATED_STUDENT_ID;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.PASSWORD_IS_DIFFERENT_FROM_CHECK;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.request.SignUpRequest;
import com.keunsori.keunsoriserver.domain.member.dto.response.SignUpResponse;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResponse registerMember(SignUpRequest signUpRequest) {
        //학번 중복체크
        final boolean isDuplicatedStudentId = memberRepository.existsByStudentId(signUpRequest.studentId());
        if (isDuplicatedStudentId) {
            throw new MemberException(DUPLICATED_STUDENT_ID);
        }

        //이메일 중복체크
        final boolean isDuplicatedEmail = memberRepository.existsByEmail(signUpRequest.email());
        if (isDuplicatedEmail) {
            throw new MemberException(DUPLICATED_EMAIL);
        }

        //비밀번호, 비밀번호 확인 일치하는지 확인
        if(!signUpRequest.password().equals(signUpRequest.passwordConfirm())) {
            throw new MemberException(PASSWORD_IS_DIFFERENT_FROM_CHECK);
        }

        //비밀번호 암호화
        String encodePassword = passwordEncoder.encode(signUpRequest.password());

        //암호화된 비밀번호로 새로운 member 생성
        Member member = new Member(signUpRequest.studentId(), signUpRequest.email(), encodePassword, signUpRequest.name(), MemberStatus.승인대기);

        //Member 저장
        Member savedMember = memberRepository.save(member);

        return new SignUpResponse(savedMember.getName(), savedMember.getStudentId(), savedMember.getEmail());
    }
}
