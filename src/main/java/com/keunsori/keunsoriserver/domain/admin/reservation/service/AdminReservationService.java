package com.keunsori.keunsoriserver.domain.admin.reservation.service;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleUpdateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.DailyAvailableResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.validator.ReservationValidator;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.ReservationException;
import com.keunsori.keunsoriserver.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

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
    public void saveWeeklySchedule(List<WeeklyScheduleUpdateRequest> requests) {
        requests.stream()
                .map(WeeklyScheduleUpdateRequest::toEntity)
                .forEach(weeklyScheduleRepository::save);
    }

    @Transactional
    public void saveDailySchedule(DailyScheduleUpdateOrCreateRequest request) {
        DailySchedule dailySchedule = DailySchedule.builder()
                .date(request.date())
                .isActive(request.isActive())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        validateNotPastDateSchedule(dailySchedule);
        validateScheduleTime(dailySchedule.getStartTime(),dailySchedule.getEndTime());

        // active -> unactive 시 예약들 삭제
        if(!dailySchedule.isActive()){
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

    public List<DailyAvailableResponse> findDailyAvailableByMonth(String yearMonth) {

        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(1);

        return Stream.iterate(start, date -> date.isBefore(end), date -> date.plusDays(1))
                .map(this::convertDateToDailyAvailableResponse).toList();
    }

    private DailyAvailableResponse convertDateToDailyAvailableResponse(LocalDate date) {
        return dailyScheduleRepository.findByDate(date)
                .map(DailyAvailableResponse::from)
                .orElseGet(() -> weeklyScheduleRepository.findByDayOfWeek(date.getDayOfWeek())
                        .map(schedule -> DailyAvailableResponse.of(date, schedule))
                        .orElseGet(() -> DailyAvailableResponse.createInactiveDate(date)));
    }

    private void validateNotPastDateSchedule(DailySchedule schedule){
        if(schedule.isPastDate()) {
            throw new ReservationException(INVALID_DATE_SCHEDULE);
        }
    }

    private void validateScheduleTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new ReservationException(INVALID_SCHEDULE_TIME);
        }
    }
}
