package com.keunsori.keunsoriserver.domain.admin.domain;

import com.keunsori.keunsoriserver.domain.admin.domain.vo.Day;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklySchedule {

    @Id
    @Column(nullable = false, unique = true)
    private String dayOfWeek;

    private boolean isActive;

    private LocalTime startTime;

    private LocalTime endTime;
}
