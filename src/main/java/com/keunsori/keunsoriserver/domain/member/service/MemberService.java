package com.keunsori.keunsoriserver.domain.member.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.MEMBER_NOT_EXISTS_WITH_STUDENT_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void deleteMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        // 회원과 연결된 과거 예약의 외래 키를 null로 설정
        reservationRepository.unlinkMemberFromPreviousReservations(id, LocalDate.now(), LocalTime.now());

        reservationRepository.deleteFutureReservationByMember(member, LocalDate.now(), LocalTime.now());

        memberRepository.delete(member);
    }
}
