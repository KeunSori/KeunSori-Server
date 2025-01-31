package com.keunsori.keunsoriserver.domain.admin.dto.response;

import java.time.LocalDate;

public record AvailableDateTimeResponse(
        LocalDate date,
        String startTime,
        String endTime
) {}
