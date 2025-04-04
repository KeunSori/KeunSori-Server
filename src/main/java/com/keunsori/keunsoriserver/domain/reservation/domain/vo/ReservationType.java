package com.keunsori.keunsoriserver.domain.reservation.domain.vo;

import com.keunsori.keunsoriserver.global.exception.ReservationException;

import static com.keunsori.keunsoriserver.global.exception.ErrorCode.*;

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
