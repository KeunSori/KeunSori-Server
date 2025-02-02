package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record DailyScheduleRequest(
        @Schema(example = "2025-01-01", type = "string")
        LocalDate date,
        boolean isActive,
        @Schema(example = "10:00", type = "string")
        LocalTime startTime,
        @Schema(example = "22:00", type = "string")
        LocalTime endTime
) {
}
