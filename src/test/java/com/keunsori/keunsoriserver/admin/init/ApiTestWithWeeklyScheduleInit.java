package com.keunsori.keunsoriserver.admin.init;

import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ApiTestWithWeeklyScheduleInit extends ApiTest {

    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @BeforeEach
    void setupWeeklySchedule() {
        weeklyScheduleRepository.deleteAll();

        for (DayOfWeek day : DayOfWeek.values()) {
            WeeklySchedule schedule = WeeklySchedule.builder()
                    .dayOfWeek(day)
                    .isActive(true)
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(23, 0))
                    .build();
            weeklyScheduleRepository.save(schedule);
        }
    }
}
