package com.keunsori.keunsoriserver.domain.reservation.dto.requset;

import jakarta.validation.constraints.NotBlank;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.global.annotation.ValidEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationCreateRequest(
        @ValidEnum(enumClass = ReservationType.class, message = "[TEAM, PERSONAL] 중에 입력해주세요. (대소문자 구분 없음)")
        String reservationType,
        @ValidEnum(enumClass = Session.class, message = "[VOCAL, DRUM, GUITAR, BASS, KEYBOARD] 중에 입력해주세요. (대소문자 구분 없음)")
        String reservationSession,
        @Schema(example = "2025-01-01", type = "string")
        LocalDate reservationDate,
        @Schema(example = "20:00", type = "string")
        LocalTime reservationStartTime,
        @Schema(example = "21:00", type = "string")
        LocalTime reservationEndTime
) {
    public Reservation toEntity(Member member) {
        ReservationType reservationType = ReservationType.from(reservationType());
        return Reservation.builder()
                .reservationType(reservationType)
                .session(reservationType.equals(ReservationType.TEAM) ? Session.ALL : Session.from(reservationSession))
                .date(reservationDate)
                .startTime(reservationStartTime)
                .endTime(reservationEndTime)
                .member(member)
                .build();
    }
}