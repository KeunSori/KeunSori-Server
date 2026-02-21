package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import java.time.LocalTime;

public record RegularReservationUpdateResponse(
        Long regularReservationId,
        LocalTime startTime,
        LocalTime endTime
) {}
