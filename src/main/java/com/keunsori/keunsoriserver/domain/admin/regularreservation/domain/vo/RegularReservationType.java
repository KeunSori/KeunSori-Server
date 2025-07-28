package com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo;

import com.keunsori.keunsoriserver.global.exception.RegularReservationException;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REGULAR_RESERVATION_TYPE;

public enum RegularReservationType {
    TEAM, LESSON;

    public static RegularReservationType from(String name) {
        try {
            return RegularReservationType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegularReservationException(INVALID_REGULAR_RESERVATION_TYPE);
        }
    }
}
