package com.keunsori.keunsoriserver.domain.reservation.dto.requset;

import jakarta.validation.constraints.NotBlank;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationCreateRequest(
        ReservationType reservationType,
        Session reservationSession,
        @NotBlank
        @Schema(example = "2025-01-01", type = "string")
        LocalDate reservationDate,
        @NotBlank
        @Schema(example = "21:00", type = "string")
        LocalTime reservationStartTime,
        @NotBlank
        @Schema(example = "21:00", type = "string")
        LocalTime reservationEndTime
) {
    public Reservation toEntity(Member member) {
        return Reservation.builder()
                .reservationType(reservationType)
                .session(reservationSession)
                .date(reservationDate)
                .startTime(reservationStartTime)
                .endTime(reservationEndTime)
                .member(member)
                .build();
    }
}
