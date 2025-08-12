package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.global.util.DayOfWeekUtil;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record WeeklyScheduleResponse(
        int dayOfWeekNum, // 0 = 일요일, 1 = 월요일, ...
        boolean isActive,
        String startTime,
        String endTime,
        List<RegularReservationResponse> regularReservations

) {
    public static WeeklyScheduleResponse from(WeeklySchedule weeklySchedule, List<RegularReservationResponse> regularReservations) {
        return new WeeklyScheduleResponse(
                DayOfWeekUtil.getCustomDayValue(weeklySchedule.getDayOfWeek()),
                weeklySchedule.isActive(),
                weeklySchedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                weeklySchedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                regularReservations
        );
    }

    public static WeeklyScheduleResponse createInactiveDay(DayOfWeek day, List<RegularReservationResponse> regularReservations) {
        return new WeeklyScheduleResponse(
                DayOfWeekUtil.getCustomDayValue(day),
                false,
                "10:00",
                "23:00",
                regularReservations
        );
    }
}
