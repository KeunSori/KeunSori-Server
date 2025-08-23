package com.keunsori.keunsoriserver.domain.admin.reservation.repository;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RegularReservationRepository extends JpaRepository<RegularReservation, Long> {

    @Query("""
            SELECT r
              FROM RegularReservation r
             WHERE r.applyEndDate >= CURRENT_DATE
             ORDER BY r.dayOfWeek, r.applyStartDate, r.startTime
            """)
    List<RegularReservation> findAllAfterToday();

    boolean existsByMember(Member member);

    @Query("""
            SELECT COUNT(r) > 0
            FROM RegularReservation r
            WHERE r.dayOfWeek = :day
            AND r.session = :session
            AND r.endTime > :start
            AND r.startTime < :end
            AND r.applyEndDate >= :applyStartDate
            AND r.applyStartDate <= :applyEndDate
            """)
    boolean existsOverlapOnTemplates(@Param("day") DayOfWeek dayOfWeek,
                                     @Param("session") Session session,
                                     @Param("start") LocalTime startTime,
                                     @Param("end") LocalTime endTime,
                                     @Param ("applyStartDate") LocalDate applyStartDate,
                                     @Param ("applyEndDate") LocalDate applyEndDate);
}
