package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.reservation.dto.response.ReservationResponse;

import java.util.List;

public record MonthlyScheduleResponse(
        List<DailyAvailableResponse> dailyAvailableRespons,
        List<ReservationResponse> reservationResponses
) {
}
