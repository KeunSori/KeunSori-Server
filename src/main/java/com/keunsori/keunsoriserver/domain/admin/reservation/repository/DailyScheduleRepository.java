package com.keunsori.keunsoriserver.domain.admin.reservation.repository;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyScheduleRepository extends JpaRepository<DailySchedule, LocalDate> {
    DailySchedule getByDate(LocalDate date);
}
