package com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo.RegularReservationType;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public record RegularReservationResponse(
        Long regularReservationId,
        DayOfWeek dayOfWeek,
        String regularReservationStartTime,
        String regularReservationEndTime,
        RegularReservationType regularReservationType,
        String regularReservationTeamName,
        Long reservationMemberId,
        String reservationStudentId
) {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static RegularReservationResponse from(RegularReservation regularReservation){
        return new RegularReservationResponse(
                regularReservation.getId(),
                regularReservation.getDayOfWeek(),
                regularReservation.getStartTime().format(TIME_FORMATTER),
                regularReservation.getEndTime().format(TIME_FORMATTER),
                regularReservation.getRegularReservationType(),
                regularReservation.getRegularReservationTeamName(),
                regularReservation.getMemberId(),
                regularReservation.getStudentId()
        );
    }
}
