package com.keunsori.keunsoriserver.domain.admin.reservation.repository;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DailyScheduleRepository extends JpaRepository<DailySchedule, LocalDate> {

    @Query("""
        SELECT d
          FROM DailySchedule d
         WHERE d.date BETWEEN :startDate AND :endDate
    """)
    List<DailySchedule> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
