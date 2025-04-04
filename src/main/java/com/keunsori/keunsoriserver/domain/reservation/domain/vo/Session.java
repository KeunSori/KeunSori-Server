package com.keunsori.keunsoriserver.domain.reservation.domain.vo;

import com.keunsori.keunsoriserver.global.exception.ReservationException;

import static com.keunsori.keunsoriserver.global.exception.ErrorCode.*;

public enum Session {
    VOCAL, KEYBOARD, DRUM, GUITAR, BASS, ALL;

    public static Session from(String name) {
        try {
            return Session.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReservationException(INVALID_SESSION);
        }
    }
}
