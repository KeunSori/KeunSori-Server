package com.keunsori.keunsoriserver.domain.member.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.MemberApprovalResponse;
import com.keunsori.keunsoriserver.domain.member.dto.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private MemberRepository memberRepository;

    // 회원 관리 리스트
    public List<MemberResponse> findAllMember(){
        return memberRepository.findAll().stream()
                .filter(member -> member.getStatus() == MemberStatus.일반)
                .map(MemberResponse::fromEntity)
                .toList();
    }

    // 가입승인 대기 리스트
    public List<MemberApprovalResponse> findAllWaiting(){
        return memberRepository.findAll().stream()
                .filter(member -> member.getStatus() == MemberStatus.승인대기)
                .map(MemberApprovalResponse::fromEntity)
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
