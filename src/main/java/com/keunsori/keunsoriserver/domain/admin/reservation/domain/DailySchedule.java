package com.keunsori.keunsoriserver.domain.admin.reservation.domain;

import com.keunsori.keunsoriserver.global.exception.ReservationException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_RESERVATION_TIME;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySchedule {

    @Id
    private LocalDate date;

    private boolean isActive;

    private LocalTime startTime;

    private LocalTime endTime;

    @Builder
    private DailySchedule(LocalDate date, boolean isActive, LocalTime startTime, LocalTime endTime){
        this.date = date;
        this.isActive = isActive;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isPastDate(){
        return date.isBefore(LocalDate.now());
    }

    public boolean isTimeWithin(LocalTime startTime, LocalTime endTime) {
        return !startTime.isBefore(this.startTime) && !endTime.isAfter(this.endTime);
    }

    public void validateTimeWithin(LocalTime startTime, LocalTime endTime) {
        if (!isTimeWithin(startTime, endTime)) {
            throw new ReservationException(INVALID_RESERVATION_TIME);
        }
    }
}
