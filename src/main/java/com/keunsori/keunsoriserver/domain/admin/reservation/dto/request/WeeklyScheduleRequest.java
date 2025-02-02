package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record WeeklyScheduleRequest(
        String dayOfWeek,
        boolean isActive,
        @Schema(example = "10:00", type = "string")
        LocalTime startTime,
        @Schema(example = "22:00", type = "string")
        LocalTime endTime
) {}
