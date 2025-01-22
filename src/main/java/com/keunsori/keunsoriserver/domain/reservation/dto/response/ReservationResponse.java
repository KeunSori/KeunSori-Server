package com.keunsori.keunsoriserver.domain.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record ReservationResponse(
        Long reservationId,
        String session,
        LocalDate reservationDate,
        String reservationStartTime,
        String reservationEndTime,
        ReservationType reservationType,
        Long reservationMemberId,
        String reservationMemberName
) {

    public static ReservationResponse of(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getSession().name().toLowerCase(),
                reservation.getDate(),
                reservation.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm")),
                reservation.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm")),
                reservation.getType(),
                reservation.getMember().getId(),
                reservation.getMember().getName()
        );
    }
}
