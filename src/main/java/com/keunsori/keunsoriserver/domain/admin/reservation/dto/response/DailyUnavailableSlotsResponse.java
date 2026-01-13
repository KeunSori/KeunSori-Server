package com.keunsori.keunsoriserver.domain.admin.reservation.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DailyUnavailableSlotsResponse(
        LocalDate date,
        boolean isActive,
        // 예약 불가능한 시간의 슬롯(24시간을 30분단위로 나눈 48개의 슬롯) 인덱스 리스트 반환
        List<Integer> unavailableSlots
) {
    public static DailyUnavailableSlotsResponse of(LocalDate date, boolean isActive, List<Integer> unavailableSlots){
        return new DailyUnavailableSlotsResponse(
                date,
                isActive,
                List.copyOf(unavailableSlots)
        );
    }
}
