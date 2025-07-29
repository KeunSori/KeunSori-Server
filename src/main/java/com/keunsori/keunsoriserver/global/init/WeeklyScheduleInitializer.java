package com.keunsori.keunsoriserver.global.init;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class WeeklyScheduleInitializer {
    private final WeeklyScheduleRepository weeklyScheduleRepository;

    public void initializeSchedules() {
        if (weeklyScheduleRepository.count() == 0) {
            for (DayOfWeek day : DayOfWeek.values()) {
                WeeklySchedule schedule = WeeklySchedule.builder()
                        .dayOfWeek(day)
                        .isActive(false)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(23, 0))
                        .build();
                weeklyScheduleRepository.save(schedule);
            }
        }
    }

    @PostConstruct
    public void postConstruct() {
        initializeSchedules();
    }
}
