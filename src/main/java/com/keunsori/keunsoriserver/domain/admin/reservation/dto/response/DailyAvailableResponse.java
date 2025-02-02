package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record DailyAvailableResponse(
        LocalDate date,
        boolean isActive,
        LocalTime startTime,
        LocalTime endTime
) {
}
