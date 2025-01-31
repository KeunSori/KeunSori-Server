package com.keunsori.keunsoriserver.domain.admin.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class DailySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private boolean isActive;

    private LocalTime startTime;

    private LocalTime endTime;

    public DailySchedule(LocalDate date, LocalTime startTime, LocalTime endTime){
        this.isActive = true;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
