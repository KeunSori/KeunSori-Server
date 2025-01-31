package com.keunsori.keunsoriserver.domain.admin.service;

import com.keunsori.keunsoriserver.domain.admin.dto.response.MemberApplicantResponse;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.response.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.MEMBER_NOT_EXISTS_WITH_STUDENT_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    // 회원 리스트
    public List<MemberResponse> findAllMember(){
        return memberRepository.findAllByStatus(MemberStatus.일반)
                .stream().map(MemberResponse::from).toList();
    }

    // 가입 신청 리스트
    public List<MemberApplicantResponse> findAllApplicants(){
        return memberRepository.findAllByStatus(MemberStatus.승인대기)
                .stream().map(MemberApplicantResponse::from).toList();
    }

    // 가입 승인
    @Transactional
    public void approveMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));
        member.approve();
    }
}
