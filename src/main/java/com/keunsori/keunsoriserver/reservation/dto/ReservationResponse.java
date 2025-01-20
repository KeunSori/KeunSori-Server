package com.keunsori.keunsoriserver.reservation.dto;

import com.keunsori.keunsoriserver.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.reservation.domain.vo.Session;

import java.sql.Time;
import java.util.Date;

public record ReservationResponse(
        Long reservationId,
        Session session,
        Date reservationDate,
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
