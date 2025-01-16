package com.keunsori.keunsoriserver.domain.member.sign_up;

import com.keunsori.keunsoriserver.domain.member.Member;
import com.keunsori.keunsoriserver.domain.member.MemberRepository;
import com.keunsori.keunsoriserver.domain.member.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.sign_up.dto.SignUpRequestDTO;
import com.keunsori.keunsoriserver.domain.member.sign_up.dto.SignUpResponseDTO;
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
    public SignUpResponseDTO registerMember(SignUpRequestDTO signUpRequestDTO) {
        //학번 중복체크
        memberRepository.findByStudentId(signUpRequestDTO.getStudentId())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 가입된 학번입니다.");
                });

        //이메일 중복체크
        memberRepository.findByHongikgmail(signUpRequestDTO.getHongikgmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 가입된 이메일 주소입니다.");
                });

        //비밀번호 암호화
        String encodePassword = passwordEncoder.encode(signUpRequestDTO.getPassword());

        //암호화된 비밀번호로 새로운 member 생성
        Member member=new Member(signUpRequestDTO.getStudentId(), signUpRequestDTO.getHongikgmail(), encodePassword,signUpRequestDTO.getName(), MemberStatus.대기);

        //Member 저장
        Member savedMember=memberRepository.save(member);

        return new SignUpResponseDTO(savedMember.getName(), savedMember.getStudentId(), savedMember.getHongikgmail());
    };
}
