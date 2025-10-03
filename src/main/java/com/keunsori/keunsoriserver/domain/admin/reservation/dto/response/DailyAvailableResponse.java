package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public record DailyAvailableResponse(
        LocalDate date,
        boolean isActive,
        List<Integer> availableSlots // 예약 가능한 시간을 30분 단위 48개의 슬롯으로 표현
) {
    public static DailyAvailableResponse of(LocalDate date, boolean isActive, boolean[] slots){
        List<Integer> availableSlots = new ArrayList<>();
        for(int i=0; i<slots.length; ++i){
            if(slots[i]) availableSlots.add(i);
        }
        return new DailyAvailableResponse(
                date,
                isActive,
                List.copyOf(availableSlots)
        );
    }
}
