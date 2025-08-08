package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record WeeklyScheduleManagementRequest(
        @NotEmpty(message = "주간 스케줄 설정은 최소 1개 이상이어야 합니다.")
        @Valid
        List<WeeklyScheduleUpdateRequest> weeklyScheduleUpdateRequestList,

        @Valid
        List<RegularReservationCreateRequest> regularReservationCreateRequestList,

        List<Long> deleteRegularReservationIds,

        boolean force
) {}
