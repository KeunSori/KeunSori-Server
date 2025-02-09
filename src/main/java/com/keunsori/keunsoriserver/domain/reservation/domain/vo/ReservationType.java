package com.keunsori.keunsoriserver.domain.reservation.domain.vo;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_RESERVATION_TYPE;

import com.keunsori.keunsoriserver.global.exception.ReservationException;

public enum ReservationType {
    TEAM, PERSONAL, LESSON;

    public static ReservationType from(String name) {
        try {
            return ReservationType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReservationException(INVALID_RESERVATION_TYPE);
        }
    }
}
