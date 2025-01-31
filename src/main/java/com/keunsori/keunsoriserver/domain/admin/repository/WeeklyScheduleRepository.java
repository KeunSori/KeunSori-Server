package com.keunsori.keunsoriserver.domain.admin.repository;

import com.keunsori.keunsoriserver.domain.admin.domain.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, String> {

    Optional<WeeklySchedule> findByDayOfWeek(String dayOfWeek);

    void deleteById(String dayOfWeek);
}
