package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WeeklyScheduleUpdateRequest(
        int dayOfWeekNum, // 0 = 일요일, 1 = 월요일, ...
        boolean isActive,
        @Schema(example = "10:00", type = "string")
        LocalTime startTime,
        @Schema(example = "22:00", type = "string")
        LocalTime endTime
) {
        public WeeklySchedule toEntity(){
                return WeeklySchedule.builder()
                        .dayOfWeek(DayOfWeek.of((dayOfWeekNum+1)%8))
                        .isActive(isActive)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();
        }
}

