package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record DailyAvailableResponse(
        LocalDate date,
        boolean isActive,
        LocalTime startTime,
        LocalTime endTime
) {
    public static DailyAvailableResponse from(DailySchedule dailySchedule){
        return new DailyAvailableResponse(
                dailySchedule.getDate(),
                dailySchedule.isActive(),
                dailySchedule.getStartTime(),
                dailySchedule.getEndTime()
        );
    }

    public static DailyAvailableResponse of(LocalDate date, WeeklySchedule weeklySchedule){
        return new DailyAvailableResponse(
                date,
                weeklySchedule.isActive(),
                weeklySchedule.getStartTime(),
                weeklySchedule.getEndTime()
        );
    }

    public static DailyAvailableResponse createInactiveDate(LocalDate date){
        return new DailyAvailableResponse(
                date,
                false,
                null,
                null
        );
    }
}
