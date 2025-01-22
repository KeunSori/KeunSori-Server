package com.keunsori.keunsoriserver.domain.reservation.dto;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Time;
import java.time.LocalDate;

public record ReservationCreateRequest(
        ReservationType reservationType,
        Session reservationSession,
        @Schema(example = "2025-01-01", type = "string")
        LocalDate reservationDate,
        @Schema(example = "21:00:00", type = "string")
        Time reservationStartTime,
        @Schema(example = "21:00:00", type = "string")
        Time reservationEndTime
) {
    public Reservation toEntity(Member member) {
        return Reservation.builder()
                .type(reservationType)
                .session(reservationSession)
                .date(reservationDate)
                .startTime(reservationStartTime)
                .endTime(reservationEndTime)
                .member(member)
                .build();
    }
}
