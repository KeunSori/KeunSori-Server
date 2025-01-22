package com.keunsori.keunsoriserver.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMember(Member member);
}
