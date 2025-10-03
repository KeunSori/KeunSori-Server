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
        // 예약 불가능한 시간의 슬롯(24시간을 30분단위로 나눈 48개의 슬롯) 인덱스 리스트 반환
        List<Integer> unavailableSlots
) {
    public static DailyAvailableResponse of(LocalDate date, boolean isActive, boolean[] slots){
        List<Integer> unavailableSlots = new ArrayList<>();
        for(int i=0; i<slots.length; ++i){
            if(!slots[i]) unavailableSlots.add(i);
        }
        return new DailyAvailableResponse(
                date,
                isActive,
                List.copyOf(unavailableSlots)
        );
    }
}
