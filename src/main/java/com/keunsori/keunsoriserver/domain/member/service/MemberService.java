package com.keunsori.keunsoriserver.domain.member.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.MemberApprovalResponse;
import com.keunsori.keunsoriserver.domain.member.dto.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
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
    private ReservationRepository reservationRepository;

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

        // 회원과 연결된 예약의 외래 키를 null로 설정
        reservationRepository.unlinkMember(id);

        memberRepository.delete(member);
    }
}
