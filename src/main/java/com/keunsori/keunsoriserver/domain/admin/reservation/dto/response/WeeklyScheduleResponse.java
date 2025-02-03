package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;

import java.time.LocalTime;

public record WeeklyScheduleResponse(
        int dayOfWeekNum, // 0 = 일요일, 1 = 월요일, ...
        boolean isActive,
        LocalTime startTime,
        LocalTime endTime
) {
    public static WeeklyScheduleResponse from(WeeklySchedule weeklySchedule){
        return new WeeklyScheduleResponse(
                weeklySchedule.getDayOfWeek().getValue()%7,
                weeklySchedule.isActive(),
                weeklySchedule.getStartTime(),
                weeklySchedule.getEndTime()
        );
    }
}
