package com.keunsori.keunsoriserver.domain.admin.reservation.service;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.validator.ReservationValidator;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.ReservationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminReservationService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;


    public List<WeeklyScheduleResponse> findAllWeeklySchedules() {
         return weeklyScheduleRepository.findAll()
                 .stream().map(WeeklyScheduleResponse::from).toList();
    }

    @Transactional
    public void saveWeeklySchedule(List<WeeklyScheduleRequest> requests) {
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

    @Transactional
    public void saveDailySchedule(DailyScheduleRequest request) {
        DailySchedule dailySchedule = new DailySchedule(
                request.date(),
                request.isActive(),
                request.startTime(),
                request.endTime()
        );

        validateNotPastDateSchdule(dailySchedule);

        // active -> unactive 시 예약들 삭제
        if(dailySchedule.isActive() == false){
            reservationRepository.deleteAllByDate(dailySchedule.getDate());
        }

        dailyScheduleRepository.save(dailySchedule);
    }

    @Transactional
    public void deleteReservationByAdmin(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS_WITH_ID));

        // 지난 예약인지 확인
        reservationValidator.validateReservationNotComplete(reservation);

        reservationRepository.delete(reservation);
    }

    private void validateNotPastDateSchdule(DailySchedule schedule){
        if(schedule.getDate().isBefore(LocalDate.now())) {
            throw new ReservationException(INVALID_DATE_SCHEDULE);
        }
    }

    private void validateSchelduleTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new ReservationException(INVALID_SCHEDULE_TIME);
        }
    }
}
