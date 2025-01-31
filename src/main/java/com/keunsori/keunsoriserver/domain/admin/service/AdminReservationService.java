package com.keunsori.keunsoriserver.domain.admin.service;

import com.keunsori.keunsoriserver.domain.admin.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.dto.request.WeeklyScheduleRequest;
import com.keunsori.keunsoriserver.domain.admin.repository.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminReservationService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;

    @Transactional
    public void updateWeeklySchedule(List<WeeklyScheduleRequest> requests) {
        List<WeeklySchedule> weeklyScheduleList = new ArrayList<>();

        for(WeeklyScheduleRequest request : requests){
            if(request.isActive()){
                // 활성화된 요일 저장
                weeklyScheduleList.add(new WeeklySchedule(
                                request.dayOfWeek(),
                                true,
                                request.startTime(),
                                request.endTime()
                        ));
            } else {
                // 비활성화된 요일 삭제
                weeklyScheduleRepository.deleteById(request.dayOfWeek());
            }
        }
        // 데이터베이스에 저장
        weeklyScheduleRepository.saveAll(weeklyScheduleList);
    }
}
