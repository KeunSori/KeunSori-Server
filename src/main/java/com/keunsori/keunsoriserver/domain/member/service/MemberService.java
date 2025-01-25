package com.keunsori.keunsoriserver.domain.member.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.response.MemberApprovalResponse;
import com.keunsori.keunsoriserver.domain.member.dto.response.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.MEMBER_NOT_EXISTS_WITH_STUDENT_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    // 회원 리스트
    public List<MemberResponse> findAllMember(){
        return memberRepository.findAllByStatus(MemberStatus.일반)
                .stream().map(MemberResponse::from).toList();
    }

    // 가입 신청 리스트
    public List<MemberApprovalResponse> findAllApplicants(){
        return memberRepository.findAllByStatus(MemberStatus.승인대기)
                .stream().map(MemberApprovalResponse::from).toList();
    }

    // 가입 승인
    @Transactional
    public void approveMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));
        member.approve();
    }

    @Transactional
    public void deleteMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        // 회원과 연결된 예약의 외래 키를 null로 설정
        reservationRepository.unlinkMember(id);

        memberRepository.delete(member);
    }
}
