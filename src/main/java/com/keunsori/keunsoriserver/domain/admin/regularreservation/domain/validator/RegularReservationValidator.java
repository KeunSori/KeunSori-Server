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

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class RegularReservationValidator {
    private final RegularReservationRepository regularReservationRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;

    // 정기 예약 생성 요청에 대한 유효성 검증
    public void validateCreateRegularReservation(RegularReservationCreateRequest request) {
        validateTimeRange(request.regularReservationStartTime(), request.regularReservationEndTime());

        DayOfWeek dayOfWeek = request.dayOfWeek();
        WeeklySchedule weeklySchedule = validateDayIsActive(dayOfWeek);

        validateTimeWithinScheduleRange(request.regularReservationStartTime(), request.regularReservationEndTime(), weeklySchedule);

        validateNoOverlap(dayOfWeek, request.regularReservationStartTime(), request.regularReservationEndTime());

    }

    // 정기 예약 삭제 시 팀장 또는 관리자 여부 검증
    public void validateDeletable(RegularReservation regularReservation, Member loginMember){
        if(!isTeamLeaderOrAdmin(regularReservation, loginMember)){
            throw new RegularReservationException(REGULAR_RESERVATION_NOT_DELETABLE);
        }
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

    private void validateTimeWithinScheduleRange(LocalTime startTime, LocalTime endTime, WeeklySchedule weeklySchedule) {
        if (startTime.isBefore(weeklySchedule.getStartTime()) || endTime.isAfter(weeklySchedule.getEndTime())) {
            throw new RegularReservationException(INVALID_REGULAR_RESERVATION_TIME);
        }
    }

    private void validateNoOverlap(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if(regularReservationRepository.existsOverlapReservation(dayOfWeek, startTime, endTime)){
            throw new RegularReservationException(ANOTHER_REGULAR_RESERVATION_ALREADY_EXISTS);
        }
    }

    private boolean isTeamLeaderOrAdmin(RegularReservation regularReservation, Member loginMember){
        return regularReservation.hasMember(loginMember) || loginMember.isAdmin();
    }
}


