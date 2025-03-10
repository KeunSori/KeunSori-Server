package com.keunsori.keunsoriserver.domain.admin.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklySchedule {

    @Id
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private boolean isActive;

    private LocalTime startTime;

    private LocalTime endTime;

    @Builder
    private WeeklySchedule(DayOfWeek dayOfWeek, boolean isActive, LocalTime startTime, LocalTime endTime){
        this.dayOfWeek = dayOfWeek;
        this.isActive = isActive;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
