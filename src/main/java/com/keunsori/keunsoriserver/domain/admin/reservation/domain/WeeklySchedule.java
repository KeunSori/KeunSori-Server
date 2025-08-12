package com.keunsori.keunsoriserver.domain.admin.reservation.domain;

import com.keunsori.keunsoriserver.global.exception.RegularReservationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REGULAR_RESERVATION_TIME;

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

    public boolean isTimeWithin(LocalTime startTime, LocalTime endTime) {
        return !startTime.isBefore(this.startTime) && !endTime.isAfter(this.endTime);
    }

    public void validateTimeWithin(LocalTime startTime, LocalTime endTime) {
        if (!isTimeWithin(startTime, endTime)) {
            throw new RegularReservationException(INVALID_REGULAR_RESERVATION_TIME);
        }
    }
}
