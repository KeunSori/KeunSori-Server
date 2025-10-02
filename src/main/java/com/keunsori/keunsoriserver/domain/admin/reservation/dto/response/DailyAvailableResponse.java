package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public record DailyAvailableResponse(
        LocalDate date,
        boolean isActive,
        boolean[] availableSlots // 예약 가능한 시간을 30분 단위 48개의 슬롯으로 표현
) {}
