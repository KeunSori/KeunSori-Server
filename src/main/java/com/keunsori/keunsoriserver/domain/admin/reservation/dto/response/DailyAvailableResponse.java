package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public record DailyAvailableResponse(
        LocalDate date,
        boolean isActive,
        String startTime,
        String endTime
) {
    public static DailyAvailableResponse from(DailySchedule dailySchedule){
        return new DailyAvailableResponse(
                dailySchedule.getDate(),
                dailySchedule.isActive(),
                dailySchedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                dailySchedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
    }

    public static DailyAvailableResponse of(LocalDate date, WeeklySchedule weeklySchedule){
        return new DailyAvailableResponse(
                date,
                weeklySchedule.isActive(),
                weeklySchedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                weeklySchedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
    }

    public static DailyAvailableResponse createInactiveDate(LocalDate date){
        return new DailyAvailableResponse(
                date,
                false,
                "10:00",
                "23:00"
        );
    }
}
