package com.keunsori.keunsoriserver.reservation.dto;

import com.keunsori.keunsoriserver.member.Member;
import com.keunsori.keunsoriserver.reservation.Reservation;
import com.keunsori.keunsoriserver.reservation.vo.ReservationType;
import com.keunsori.keunsoriserver.reservation.vo.Session;

import java.sql.Time;
import java.util.Date;

public record ReservationCreateRequest(
        ReservationType reservationType,
        Session reservationSession,
        Date reservationDate,
        Time reservationStartTime,
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
