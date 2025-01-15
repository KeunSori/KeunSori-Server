package com.keunsori.keunsoriserver.member;

import com.keunsori.keunsoriserver.member.Dto.MemberRequestDto;
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

    public Member findById(Long id){
        return memberRepository.findById(id).orElse(null);
    }

    @Transactional
    public Member deleteMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Member not found."));
        memberRepository.delete(member);
        return member;
    }

    @Transactional
    public void approveMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Member not found."));
        member.approve();
    }
}
