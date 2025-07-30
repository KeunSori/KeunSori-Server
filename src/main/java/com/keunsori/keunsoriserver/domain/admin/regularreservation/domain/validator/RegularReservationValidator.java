package com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.validator;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.repository.RegularReservationRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.global.exception.RegularReservationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class RegularReservationValidator {
    private final RegularReservationRepository regularReservationRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;

    // 정기 예약 생성 요청에 대한 유효성 검증
    public void validateCreateRegularReservation(RegularReservationCreateRequest request) {
        validateTimeRange(request.regularReservationStartTime(), request.regularReservationEndTime());

        // 요일별 스케줄 존재 및 활성화 여부 검증
        WeeklySchedule weeklySchedule = validateDayIsActive(request.dayOfWeek());

        // 스케줄 시간 범위 내에 있는지 검증
        weeklySchedule.validateTimeWithin(request.regularReservationStartTime(), request.regularReservationEndTime());

        // 중복 예약 여부 검증
        validateNoOverlap(request.dayOfWeek(), request.regularReservationStartTime(), request.regularReservationEndTime());
    }

    // 정기 예약 삭제 시 팀장 또는 관리자 여부 검증
    public void validateDeletable(RegularReservation regularReservation, Member loginMember){
        if (!loginMember.isAdmin()) {
            regularReservation.validateReservedBy(loginMember);
        }
    }

    // 정기 예약 엔티티 다중 삭제 시 선택 수와 조회 수가 같은지 검증
    public List<RegularReservation> validateAndGetAllExists(List<Long> ids) {
        List<RegularReservation> found = regularReservationRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new RegularReservationException(PARTIAL_REGULAR_RESERVATION_MISSING);
        }
        return found;
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if(startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw new RegularReservationException(INVALID_REGULAR_RESERVATION_TIME);
        }
    }

    private WeeklySchedule validateDayIsActive(DayOfWeek dayOfWeek){
        return weeklyScheduleRepository.findById(dayOfWeek)
                .orElseThrow(() -> new RegularReservationException(INVALID_REGULAR_RESERVATION_DATE));
    }


    private void validateNoOverlap(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if(regularReservationRepository.existsOverlapReservation(dayOfWeek, startTime, endTime)){
            throw new RegularReservationException(ANOTHER_REGULAR_RESERVATION_ALREADY_EXISTS);
        }
    }
}


