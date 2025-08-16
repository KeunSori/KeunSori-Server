package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record WeeklyScheduleManagementRequest(
        @Valid
        List<WeeklyScheduleUpdateRequest> weeklyScheduleUpdateRequestList,

        @Valid
        List<RegularReservationCreateRequest> regularReservationCreateRequestList,

        List<Long> deleteRegularReservationIds
) {}
