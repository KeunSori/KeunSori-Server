package com.keunsori.keunsoriserver.domain.reservation.domain.vo;

public enum Session {
    VOCAL, KEYBOARD, DRUM, GUITAR, BASS, ALL;

    public static Session from(String name) {
        return Session.valueOf(name.toUpperCase());
    }
}
