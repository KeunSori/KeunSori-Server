package com.keunsori.keunsoriserver.domain.reservation.repository;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.vo.Session;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMemberOrderByDateDescStartTimeDesc(Member member);

    List<Reservation> findAllByDate(LocalDate date);

    List<Reservation> findAllByDateBetweenOrderByDateAscStartTimeAsc(LocalDate start, LocalDate end);

    void deleteAllByDate(LocalDate date);

    void deleteAllByRegularReservation(RegularReservation regularReservation);

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
    void unlinkMember(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(r) > 0 "
            + "FROM Reservation r "
            + "WHERE r.date = :date "
            + "AND (r.reservationType != 'PERSONAL' OR (r.reservationType = 'PERSONAL' AND r.session = :session)) "
            + "AND (r.startTime BETWEEN :start_time AND :end_time "
            + "OR   r.endTime BETWEEN :start_time AND :end_time "
            + "OR   :start_time BETWEEN r.startTime AND r.endTime)")
    boolean existsAnotherReservationAtDateAndTimePeriodWithSession(@Param("date") LocalDate date, @Param("session") Session session, @Param("start_time") LocalTime startTime, @Param("end_time") LocalTime endTime);

    @Modifying
    @Query("""
        DELETE FROM Reservation r
        WHERE  r.date IN: dates
        AND r.startTime < :endTime
        AND r.endTime > :startTime
""")
    void deleteAllByDateInAndTimeRange(@Param("dates") List<LocalDate> dates, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);
}
