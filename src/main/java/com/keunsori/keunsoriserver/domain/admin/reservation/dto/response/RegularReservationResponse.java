package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public record RegularReservationResponse(
        Long regularReservationId,
        DayOfWeek dayOfWeek,
        String regularReservationStartTime,
        String regularReservationEndTime,
        ReservationType regularReservationType,
        Session regularReservationSession,
        String regularReservationTeamName,
        String regularReservationApplyStartDate,
        String regularReservationApplyEndDate,
        Long reservationMemberId,
        String reservationMemberStudentId
) {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static RegularReservationResponse from(RegularReservation regularReservation){
        return new RegularReservationResponse(
                regularReservation.getId(),
                regularReservation.getDayOfWeek(),
                regularReservation.getStartTime().format(TIME_FORMATTER),
                regularReservation.getEndTime().format(TIME_FORMATTER),
                regularReservation.getReservationType(),
                regularReservation.getSession(),
                regularReservation.getRegularReservationTeamName(),
                regularReservation.getApplyStartDate().format(DATE_TIME_FORMATTER),
                regularReservation.getApplyEndDate().format(DATE_TIME_FORMATTER),
                regularReservation.getTeamLeaderId(),
                regularReservation.getTeamLeaderStudentId()
        );
    }
}
