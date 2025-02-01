package com.keunsori.keunsoriserver.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMemberOrderByDateDesc(Member member);

    List<Reservation> findAllByDateBetweenOrderByDateAscStartTimeAsc(LocalDate start, LocalDate end);

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

    @Query("SELECT COUNT(r) > 0 "
         + "FROM Reservation r "
         + "WHERE r.date = :date "
         + "AND (r.session = :session OR r.reservationType = 'TEAM') "
         + "AND (r.startTime BETWEEN :start_time AND :end_time "
         + "OR   r.endTime BETWEEN :start_time AND :end_time "
         + "OR   :start_time BETWEEN r.startTime AND r.endTime)")
    boolean existsAnotherReservationAtDateAndTimePeriodWithSession(@Param("date") LocalDate date, @Param("session") Session session, @Param("start_time") LocalTime startTime, @Param("end_time") LocalTime endTime);
}
