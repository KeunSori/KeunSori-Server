package com.keunsori.keunsoriserver.domain.admin.regularreservation.repository;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.RegularReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface RegularReservationRepository extends JpaRepository<RegularReservation, Long> {
    List<RegularReservation> findAllByDayOfWeek(DayOfWeek dayOfWeek);

    List<RegularReservation> findAllByOrderByDayOfWeekAscStartTimeAsc();

    // 팀장 학번으로 정기예약 조회 (팀장 예약 현황에서 사용)
    List<RegularReservation> findAllByMember_StudentId(String studentId);


    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM RegularReservation r
        WHERE r.dayOfWeek = :dayOfWeek
        AND r.startTime < :endTime
        AND r.endTime > :startTime
        """)
    boolean existsOverlapReservation(
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );

}
