package com.keunsori.keunsoriserver.domain.admin.repository;

import com.keunsori.keunsoriserver.domain.admin.domain.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, String> {
    void deleteById(String dayOfWeek);
}
