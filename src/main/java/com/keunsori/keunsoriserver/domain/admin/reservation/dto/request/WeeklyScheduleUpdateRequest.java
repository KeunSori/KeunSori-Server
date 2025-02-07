package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.global.util.DayOfWeekUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record WeeklyScheduleUpdateRequest(
        @NotNull
        @Min(value = 0) @Max(value = 7)
        int dayOfWeekNum, // 0 = 일요일, 1 = 월요일, ...
        boolean isActive,
        @Schema(example = "10:00", type = "string")
        LocalTime startTime,
        @Schema(example = "22:00", type = "string")
        LocalTime endTime
) {
        public WeeklySchedule toEntity(){
                return WeeklySchedule.builder()
                        .dayOfWeek(DayOfWeekUtil.fromCustomDayValue(dayOfWeekNum))
                        .isActive(isActive)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();
        }
}

