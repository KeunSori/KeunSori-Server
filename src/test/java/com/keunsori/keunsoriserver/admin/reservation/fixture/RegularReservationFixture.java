package com.keunsori.keunsoriserver.admin.reservation.fixture;

import com.keunsori.keunsoriserver.admin.member.fixture.MemberFixture;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class RegularReservationFixture {

    public static RegularReservation PAST_REGULAR_RESERVATION_FOR_MONDAY(Member member) {
        return createRegularReservation(
                ReservationType.TEAM,
                Session.ALL,
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 2, 28),
                member
        );
    }

    public static RegularReservation TODAY_START_REGULAR_RESERVATION_FOR_MONDAY(Member member) {
        return createRegularReservation(
                ReservationType.TEAM,
                Session.ALL,
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalDate.now(),
                LocalDate.of(2999,12,31),
                member
        );
    }

    public static RegularReservation TODAY_END_REGULAR_RESERVATION_FOR_MONDAY(Member member) {
        return createRegularReservation(
                ReservationType.TEAM,
                Session.ALL,
                DayOfWeek.MONDAY,
                LocalTime.of(13, 0),
                LocalTime.of(15, 0),
                LocalDate.of(2020,1, 1),
                LocalDate.now(),
                member
        );
    }

    public static RegularReservation FUTURE_REGULAR_RESERVATION_FOR_MONDAY(Member member) {
        return createRegularReservation(
                ReservationType.TEAM,
                Session.ALL,
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalDate.of(2999,1, 1),
                LocalDate.of(2999,12,31),
                member
        );
    }


    private static RegularReservation createRegularReservation(
            ReservationType reservationType,
            Session reservationSession,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            LocalDate applyStartDate,
            LocalDate applyEndDate,
            Member member
    ) {
        return RegularReservation.builder()
                .reservationType(reservationType)
                .session(reservationType == ReservationType.TEAM ? Session.ALL : reservationSession)
                .dayOfWeek(dayOfWeek)
                .regularReservationTeamName("사무라이하트")
                .startTime(startTime)
                .endTime(endTime)
                .applyStartDate(applyStartDate)
                .applyEndDate(applyEndDate)
                .member(member)
                .build();
    }

}
