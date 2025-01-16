package com.keunsori.keunsoriserver.member;

import com.keunsori.keunsoriserver.member.Dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private MemberRepository memberRepository;

    public List<MemberResponseDto> findAll(){
        return memberRepository.findAll().stream().map(MemberResponseDto::fromEntity)
                .toList();
    }

    @Transactional
    public Member approveMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        member.approve();
        return member;
    }

    @Transactional
    public void deleteMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        memberRepository.delete(member);
    }
}
