package com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo;

import com.keunsori.keunsoriserver.global.exception.RegularReservationException;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REGULAR_RESERVATION_SESSION;

public enum RegularReservationSession {
    VOCAL, KEYBOARD, DRUM, GUITAR, BASS, ALL;

    public static RegularReservationSession from(String name) {
        try {
            return RegularReservationSession.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegularReservationException(INVALID_REGULAR_RESERVATION_SESSION);
        }
    }
}
