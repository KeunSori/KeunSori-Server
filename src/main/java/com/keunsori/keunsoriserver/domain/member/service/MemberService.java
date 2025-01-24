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

    // 회원 리스트
    @Transactional(readOnly = true)
    public List<MemberResponse> findAllMember(){
        return memberRepository.findAllByStatus(MemberStatus.일반)
                .stream().map(MemberResponse::of).toList();
    }

    // 가입 신청 리스트
    @Transactional(readOnly = true)
    public List<MemberApprovalResponse> findAllWaiting(){
        return memberRepository.findAllByStatus(MemberStatus.승인대기)
                .stream().map(MemberApprovalResponse::of).toList();
    }

    // 가입 승인
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

        // 연관 데이터 삭제 로직 추가 필요
        memberRepository.delete(member);
    }
}
