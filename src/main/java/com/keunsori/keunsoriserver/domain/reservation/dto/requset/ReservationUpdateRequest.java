package com.keunsori.keunsoriserver.domain.reservation.dto.requset;

import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationUpdateRequest(
        ReservationType reservationType,
        Session reservationSession,
        @Schema(example = "2025-01-01", type = "string")
        LocalDate reservationDate,
        @Schema(example = "20:00", type = "string")
        LocalTime reservationStartTime,
        @Schema(example = "21:00", type = "string")
        LocalTime reservationEndTime
) {}
