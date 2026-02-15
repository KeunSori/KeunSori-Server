package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RegularReservationUpdateRequest(
        @NotNull
        Long regularReservationId,

        @NotNull
        @Schema(example = "15:00", type = "string")
        LocalTime regularReservationStartTime,

        @NotNull
        @Schema(example = "16:00", type = "string")
        LocalTime regularReservationEndTime
) {}
