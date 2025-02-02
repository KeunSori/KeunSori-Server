package com.keunsori.keunsoriserver.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMember(Member member);

    List<Reservation> findAllByDateBetween(LocalDate start, LocalDate end);

    List<Reservation> deleteAllByDate(LocalDate date);



    @Query("SELECT COUNT(r) > 0 "
         + "FROM Reservation r "
         + "WHERE r.date = :date "
         + "AND (r.startTime BETWEEN :start_time AND :end_time "
         + "OR   r.endTime BETWEEN :start_time AND :end_time "
         + "OR   :start_time BETWEEN r.startTime AND r.endTime)")
    boolean existsAnotherReservationAtDateAndTimePeriod(@Param("date") LocalDate date, @Param("start_time") LocalTime startTime, @Param("end_time") LocalTime endTime);

    // 탈퇴된 회원 외래키 null 처리 쿼리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.member = null WHERE r.member.id = :memberId")
    void unlinkMember(Long memberId);
}
