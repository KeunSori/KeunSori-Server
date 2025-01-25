package com.keunsori.keunsoriserver.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMember(Member member);
    List<Reservation> findAllByDateBetween(LocalDate start, LocalDate end);

    // 탈퇴된 회원 외래키 null 처리 쿼리
    @Modifying(clearAutomatically=true)
    @Query("UPDATE Reservation r SET r.member = null WHERE r.member.id = :memberId")
    void unlinkMember(Long memberId);
}
