package com.keunsori.keunsoriserver.domain.admin.service;

import com.keunsori.keunsoriserver.domain.admin.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.dto.request.WeeklyScheduleRequest;
import com.keunsori.keunsoriserver.domain.admin.dto.response.AvailableDateTimeResponse;
import com.keunsori.keunsoriserver.domain.admin.dto.response.DailyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.dto.response.WeeklyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.repository.WeeklyScheduleRepository;
import com.keunsori.keunsoriserver.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminReservationService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;

    public List<AvailableDateTimeResponse> findAllAvailableDateTimes(String yearMonth) {
        List<AvailableDateTimeResponse> responses = new ArrayList<>();

        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(1);

        List<DailySchedule> dailySchedules = dailyScheduleRepository.findAll();
        for(LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            Optional<DailySchedule> dailyScheduleOpt = dailyScheduleRepository.findByDate(date);

            // 일간 설정이 있으면 적용
            if(dailyScheduleOpt.isPresent()) {
                DailySchedule dailySchedule = dailyScheduleOpt.get();
                responses.add(new AvailableDateTimeResponse(
                        date,
                        dailySchedule.isActive(),
                        dailySchedule.getStartTime(),
                        dailySchedule.getEndTime()
                ));
            } else {
                String dayOfWeek = date.getDayOfWeek().toString();
                Optional<WeeklySchedule> weeklyScheduleOpt = weeklyScheduleRepository.findByDayOfWeek(dayOfWeek);

                // 일간 설정이 없으면 주간 설정 적용
                if(weeklyScheduleOpt.isPresent()){
                    WeeklySchedule weeklySchedule = weeklyScheduleOpt.get();
                    responses.add(new AvailableDateTimeResponse(
                            date,
                            weeklySchedule.isActive(),
                            weeklySchedule.getStartTime(),
                            weeklySchedule.getEndTime()
                    ));
                } else {
                    // 주간 설정도 없으면 사용 불가
                    responses.add(new AvailableDateTimeResponse(
                            date,
                            false,
                            null,
                            null
                    ));
                }
            }
        }
        return responses;
    }

    public List<WeeklyScheduleResponse> findAllWeeklySchedules() {
         return weeklyScheduleRepository.findAll()
                 .stream().map(WeeklyScheduleResponse::from).toList();
    }

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
