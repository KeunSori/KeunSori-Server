package com.keunsori.keunsoriserver.domain.reservation.dto;

import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import java.sql.Time;
import java.time.LocalDate;

public record ReservationResponse(
        Long reservationId,
        Session session,
        LocalDate reservationDate,
        Time reservationStartTime,
        Time reservationEndTime,
        ReservationType reservationType,
        Long reservationMemberId,
        String reservationMemberName
) {

    public static ReservationResponse of(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getSession(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getType(),
                reservation.getMember().getId(),
                reservation.getMember().getName()
        );
    }
}
