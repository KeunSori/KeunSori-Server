package com.keunsori.keunsoriserver.domain.admin.dto.request;

import com.keunsori.keunsoriserver.domain.admin.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.domain.vo.Day;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

public record WeeklyScheduleRequest(
        String dayOfWeek,
        boolean isActive,
        LocalTime startTime,
        LocalTime endTime
) {}
