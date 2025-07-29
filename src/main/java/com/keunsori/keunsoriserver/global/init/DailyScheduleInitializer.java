package com.keunsori.keunsoriserver.global.init;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DailyScheduleInitializer {
    private final DailyScheduleRepository dailyScheduleRepository;

    @PostConstruct
    public void initializeTodaySchedule() {
        LocalDate today = LocalDate.now();

        if (dailyScheduleRepository.findById(today).isEmpty()) {
            DailySchedule schedule = DailySchedule.builder()
                    .date(today)
                    .isActive(false)
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(23, 0))
                    .build();
            dailyScheduleRepository.save(schedule);
        }
    }
}
