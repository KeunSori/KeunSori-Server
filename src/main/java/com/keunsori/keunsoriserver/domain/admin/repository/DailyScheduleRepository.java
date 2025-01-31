package com.keunsori.keunsoriserver.domain.admin.repository;

import com.keunsori.keunsoriserver.domain.admin.domain.DailySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Long> {
    Optional<DailySchedule> findByDate(LocalDate date);
}
