package com.keunsori.keunsoriserver.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keunsori.keunsoriserver.member.Member;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMember(Member member);
}
