package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.global.util.DayOfWeekUtil;

import java.time.LocalTime;

public record WeeklyScheduleResponse(
        int dayOfWeekNum, // 0 = 일요일, 1 = 월요일, ...
        boolean isActive,
        LocalTime startTime,
        LocalTime endTime
) {
    public static WeeklyScheduleResponse from(WeeklySchedule weeklySchedule){
        return new WeeklyScheduleResponse(
                DayOfWeekUtil.getCustomDayValue(weeklySchedule.getDayOfWeek()),
                weeklySchedule.isActive(),
                weeklySchedule.getStartTime(),
                weeklySchedule.getEndTime()
        );
    }
}
