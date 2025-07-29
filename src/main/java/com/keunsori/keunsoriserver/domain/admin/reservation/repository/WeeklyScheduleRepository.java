package com.keunsori.keunsoriserver.domain.admin.reservation.repository;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, DayOfWeek> {

    WeeklySchedule getByDayOfWeek(DayOfWeek dayOfWeek);
}
