package com.keunsori.keunsoriserver.reservation.dto;

import com.keunsori.keunsoriserver.reservation.vo.ReservationType;
import com.keunsori.keunsoriserver.reservation.vo.Session;

import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Time;
import java.util.Date;

public record ReservationUpdateRequest(
        ReservationType reservationType,
        Session reservationSession,
        @Schema(example = "2025-01-01", type = "string")
        Date reservationDate,
        @Schema(example = "20:00:00", type = "string")
        Time reservationStartTime,
        @Schema(example = "21:00:00", type = "string")
        Time reservationEndTime
) {

}
