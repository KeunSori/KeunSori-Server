package com.keunsori.keunsoriserver.reservation.dto;

import com.keunsori.keunsoriserver.reservation.vo.ReservationType;
import com.keunsori.keunsoriserver.reservation.vo.Session;

import java.sql.Time;
import java.util.Date;

public record ReservationUpdateRequest(
        ReservationType reservationType,
        Session reservationSession,
        Date reservationDate,
        Time reservationStartTime,
        Time reservationEndTime
) {

}
