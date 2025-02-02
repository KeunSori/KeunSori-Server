package com.keunsori.keunsoriserver.domain.member.sign_up.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.sign_up.dto.request.SignUpRequest;
import com.keunsori.keunsoriserver.domain.member.sign_up.dto.response.SignUpResponse;
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
        final boolean validStudentId=memberRepository.existsByStudentId(signUpRequest.studentId());
        if(validStudentId) {
            throw new MemberException("이미 가입된 학번입니다.");
        }


        //이메일 중복체크
        final boolean validHongikGmail= memberRepository.existsByHongikgmail(signUpRequest.hongikgmail());
        if(validHongikGmail) {
            throw new MemberException("이미 가입된 이메일 주소입니다.");
        }

        //비밀번호, 비밀번호 확인 일치하는지 확인
        if(!signUpRequest.password().equals(signUpRequest.passwordConfirm())) {
            throw new MemberException("비밀번호가 일치하지 않습니다. 다시 입력해주세요");
        }

        //비밀번호 암호화
        String encodePassword = passwordEncoder.encode(signUpRequest.password());

        //암호화된 비밀번호로 새로운 member 생성
        Member member=new Member(signUpRequest.studentId(), signUpRequest.hongikgmail(), encodePassword, signUpRequest.name(), MemberStatus.승인대기);

        //Member 저장
        Member savedMember=memberRepository.save(member);

        return new SignUpResponse(savedMember.getName(), savedMember.getStudentId(), savedMember.getHongikgmail());
    };
}
