package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import java.util.List;

public record WeeklyScheduleManagementResponse(
        String message,
        List<RegularReservationResponse> createdRegularReservations,
        List<Long> deletedRegularReservationIds
) {
}
