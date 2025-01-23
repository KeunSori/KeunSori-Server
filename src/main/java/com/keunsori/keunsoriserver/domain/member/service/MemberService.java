package com.keunsori.keunsoriserver.domain.member.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.MemberApprovalResponse;
import com.keunsori.keunsoriserver.domain.member.dto.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.MemberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.MEMBER_NOT_EXISTS_WITH_STUDENT_ID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private MemberRepository memberRepository;

    public List<MemberResponse> findAllMember(){
        return memberRepository.findAll().stream()
                .filter(member -> member.getStatus() == MemberStatus.일반)
                .map(MemberResponse::of)
                .toList();
    }

    public List<MemberApprovalResponse> findAllWaiting(){
        return memberRepository.findAll().stream()
                .filter(member -> member.getStatus() == MemberStatus.승인대기)
                .map(MemberApprovalResponse::of)
                .toList();
    }

    @Transactional
    public Member approveMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));
        member.approve();
        return member;
    }

    @Transactional
    public void deleteMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));
        memberRepository.delete(member);
    }
}
