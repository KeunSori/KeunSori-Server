package com.keunsori.keunsoriserver.domain.admin.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailableDateTimeResponse(
        LocalDate date,
        boolean isActive,
        LocalTime startTime,
        LocalTime endTime
) {
}
