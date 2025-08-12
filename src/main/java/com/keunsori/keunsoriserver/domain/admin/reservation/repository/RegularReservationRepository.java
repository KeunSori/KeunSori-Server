package com.keunsori.keunsoriserver.domain.admin.reservation.repository;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface RegularReservationRepository extends JpaRepository<RegularReservation, Long> {
    List<RegularReservation> findAllByOrderByDayOfWeekAscStartTimeAsc();

    // 팀장 학번으로 정기예약 조회 (팀장 예약 현황에서 사용)
    List<RegularReservation> findAllByMember_StudentId(String studentId);

    List<RegularReservation> findAllByDayOfWeekOrderByStartTime(DayOfWeek dayOfWeek);


    boolean existsByMember(Member member);

    @Query("""
            SELECT COUNT(r) > 0
            FROM RegularReservation r
            WHERE r.dayOfWeek = :day
            AND r.startTime < :end
            AND r.endTime > :start
            """)
    boolean existsOverlapOnTemplates(@Param("day") DayOfWeek dayOfWeek, @Param("start") LocalTime startTime, @Param("end") LocalTime endTime);
    
}
