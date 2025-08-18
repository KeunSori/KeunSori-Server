package com.keunsori.keunsoriserver.domain.admin.reservation.domain.validator;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.RegularReservationRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.global.exception.RegularReservationException;
import com.keunsori.keunsoriserver.global.exception.ReservationException;
import com.keunsori.keunsoriserver.global.util.DayOfWeekUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class RegularReservationValidator {
    private final RegularReservationRepository regularReservationRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;

    // 정기 예약 생성 요청에 대한 유효성 검증
    public void validateCreateRegularReservation(RegularReservationCreateRequest request) {
        validateTimeRange(request.regularReservationStartTime(), request.regularReservationEndTime());

        validateDateRange(request.applyStartDate(), request.applyEndDate(), DayOfWeekUtil.fromString(request.dayOfWeek()));

        DayOfWeek day = DayOfWeek.valueOf(request.dayOfWeek());

        // 요일별 스케줄 존재 및 활성화 여부 검증
        WeeklySchedule weeklySchedule = validateDayIsActive(day);

        // 스케줄 시간 범위 내에 있는지 검증
        weeklySchedule.validateTimeWithin(request.regularReservationStartTime(), request.regularReservationEndTime());

        // 정기 예약 템플릿끼리의 중복 예약 여부 검증
        validateNoOverlapWithRegularTemplates(
                day,
                Session.from(request.reservationSession()),
                request.regularReservationStartTime(),
                request.regularReservationEndTime(),
                request.applyStartDate(),
                request.applyEndDate());
    }


    // 삭제하려는 id 가 모두 존재하는 id 인지 검증
    public void validateAllIdExists(List<Long> ids, List<RegularReservation> found) {
        if (found.size() != ids.size()) {
            throw new RegularReservationException(PARTIAL_REGULAR_RESERVATION_MISSING);
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw new RegularReservationException(INVALID_REGULAR_RESERVATION_TIME);
        }
    }

    private WeeklySchedule validateDayIsActive(DayOfWeek dayOfWeek) {
        return weeklyScheduleRepository.findById(dayOfWeek)
                .orElseThrow(() -> new RegularReservationException(INVALID_REGULAR_RESERVATION_DATE));
    }

    private void validateDateRange(LocalDate start, LocalDate end, DayOfWeek dayOfWeek) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new ReservationException(INVALID_REGULAR_RESERVATION_DATE);
        }

        if (start.isEqual(end)) {
            throw new ReservationException(APPLY_DATE_SAME_WITH_END_DATE);
        }

        int daysUntilTarget = (dayOfWeek.getValue() - start.getDayOfWeek().getValue() +7) % 7;
        LocalDate firstOccurrence = start.plusDays(daysUntilTarget);

        if (firstOccurrence.isAfter(end)) {
            throw new RegularReservationException(DAY_NOT_IN_APPLY_PERIOD);
        }
    }

    private void validateNoOverlapWithRegularTemplates(
            DayOfWeek dayOfWeek,
            Session session,
            LocalTime startTime,
            LocalTime endTime,
            LocalDate applyStartDate,
            LocalDate applyEndDate) {
        boolean exists = regularReservationRepository.existsOverlapOnTemplates(
                dayOfWeek,
                session,
                startTime,
                endTime,
                applyStartDate,
                applyEndDate);
        if (exists) {
            throw new ReservationException(ANOTHER_REGULAR_RESERVATION_ALREADY_EXISTS);
        }
    }

    private record DaySessionKey(DayOfWeek dayOfWeek, Session session) {}

    // 신규 정기 예약 리스트 내 겹침 검증
    public void validateNoOverlapAmongCreates(List<RegularReservationCreateRequest> requests){
        if (requests == null || requests.size() <= 1)
            return;

        Map<DaySessionKey, List<RegularReservationCreateRequest>> byDayAndSession =
                requests.stream().collect(Collectors.groupingBy(req -> new DaySessionKey(
                        DayOfWeekUtil.fromString(req.dayOfWeek()),
                        Session.from(req.reservationSession())
                )));

        for (Map.Entry<DaySessionKey, List<RegularReservationCreateRequest>> entry : byDayAndSession.entrySet()) {
            List<RegularReservationCreateRequest> group = entry.getValue();

            group.sort(Comparator
                    .comparing(RegularReservationCreateRequest::regularReservationStartTime)
                    .thenComparing(RegularReservationCreateRequest::regularReservationEndTime));

            for (int i = 0; i < group.size() - 1; i++) {
                RegularReservationCreateRequest a = group.get(i);
                RegularReservationCreateRequest b = group.get(i + 1);

                // 적용 기간이 겹칠 때만 시간대 비교
                boolean dateOverlap = hasDateOverlap(
                        a.applyStartDate(), a.applyEndDate(),
                        b.applyStartDate(), b.applyEndDate()
                );

                if (!dateOverlap) continue;

                boolean timeOverlap = isTimeOverlap(
                        a.regularReservationStartTime(), a.regularReservationEndTime(),
                        b.regularReservationStartTime(), b.regularReservationEndTime()
                );

                if (timeOverlap) {
                    throw new ReservationException(ANOTHER_REGULAR_RESERVATION_ALREADY_EXISTS);
                }
            }
        }
    }

    private boolean hasDateOverlap(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        return !endDate1.isBefore(startDate2) && !endDate2.isBefore(startDate1);
    }

    private boolean isTimeOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return startTime1.isBefore(endTime2) && startTime2.isBefore(endTime1);
    }
}


