package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WeeklyScheduleUpdateRequest(
        @NotBlank
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
                        .dayOfWeek(DayOfWeek.of((convertToDowValue(dayOfWeekNum))))
                        .isActive(isActive)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();
        }

        private int convertToDowValue(int dayOfWeekNum){
                if(dayOfWeekNum == 0) dayOfWeekNum = 7;
                return dayOfWeekNum;
        }
}

