package com.keunsori.keunsoriserver.domain.reservation.domain.vo;

public enum ReservationType {
    TEAM, PERSONAL;

    public static ReservationType from(String name) {
        return ReservationType.valueOf(name.toUpperCase());
    }
}
