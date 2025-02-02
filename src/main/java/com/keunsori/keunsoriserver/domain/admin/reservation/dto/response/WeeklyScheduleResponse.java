package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;

import java.time.LocalTime;

public record WeeklyScheduleResponse(
        String dayOfWeek,
        boolean isActive,
        LocalTime startTime,
        LocalTime endTime
) {
    public static WeeklyScheduleResponse from(WeeklySchedule weeklySchedule){
        return new WeeklyScheduleResponse(
                weeklySchedule.getDayOfWeek(),
                weeklySchedule.isActive(),
                weeklySchedule.getStartTime(),
                weeklySchedule.getEndTime()
        );
    }
}
