package com.keunsori.keunsoriserver.domain.reservation.domain.validator;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import org.springframework.stereotype.Component;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationUpdateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.ReservationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;

    public void validateReservationCreateRequest(ReservationCreateRequest request) {
        validateReservationDateIsNotPast(request.reservationDate());
        validateReservationTime(request.reservationStartTime(), request.reservationEndTime());
        validateReservationWithSchedule(request.reservationDate(),request.reservationStartTime(),request.reservationEndTime());
        validateOtherReservationsNotExist(
                ReservationType.from(request.reservationType()),
                Session.from(request.reservationSession()),
                request.reservationDate(),
                request.reservationStartTime(),
                request.reservationEndTime()
        );
    }

    public void validateReservationUpdateRequest(ReservationUpdateRequest request) {
        validateReservationDateIsNotPast(request.reservationDate());
        validateReservationTime(request.reservationStartTime(), request.reservationEndTime());
        validateOtherReservationsNotExist(
                ReservationType.from(request.reservationType()),
                Session.from(request.reservationSession()),
                request.reservationDate(),
                request.reservationStartTime(),
                request.reservationEndTime()
        );
    }

    public void validateReservationDeletable(Reservation reservation, Member loggedInMember) {
        validateReservationNotComplete(reservation);
        validateReservationMemberIsOwner(reservation, loggedInMember);
    }

    public void validateOriginalReservationUpdatable(Reservation reservation, Member loggedInMember) {
        validateReservationMemberIsOwner(reservation, loggedInMember);
        validateReservationNotComplete(reservation);
    }

    private void validateReservationMemberIsOwner(Reservation reservation, Member loggedInMember) {
        if (!reservation.hasMember(loggedInMember)) {
            throw new ReservationException(RESERVATION_NOT_EQUAL_MEMBER);
        }
    }

    public void validateReservationNotComplete(Reservation reservation) {
        if (reservation.isComplete()) {
            throw new ReservationException(RESERVATION_ALREADY_COMPLETED);
        }
    }

    private void validateReservationTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new ReservationException(INVALID_RESERVATION_TIME);
        }
    }

    private void validateOtherReservationsNotExist(ReservationType reservationType, Session reservationSession, LocalDate reservationDate, LocalTime reservationStartTime, LocalTime reservationEndTime) {
        if (reservationType == ReservationType.PERSONAL) {
            validatePersonalReservationTime(reservationSession, reservationDate, reservationStartTime, reservationEndTime);
            return;
        }

        validateReservationTimeIsNotDuplicatedWithAllReservations(reservationDate, reservationStartTime, reservationEndTime);
    }

    private void validateReservationTimeIsNotDuplicatedWithAllReservations(LocalDate reservationDate, LocalTime reservationStartTime, LocalTime reservationEndTime) {
        boolean isThereAnotherReservation = reservationRepository.existsAnotherReservationAtDateAndTimePeriod(
                reservationDate,
                reservationStartTime.plusMinutes(1),
                reservationEndTime.minusMinutes(1)
        );

        if (isThereAnotherReservation) {
            throw new ReservationException(ANOTHER_RESERVATION_ALREADY_EXISTS);
        }
    }

    private void validatePersonalReservationTime(Session reservationSession, LocalDate reservationDate, LocalTime reservationStartTime, LocalTime reservationEndTime) {
        boolean isThereAnotherReservationWithSameSession = reservationRepository
                .existsAnotherReservationAtDateAndTimePeriodWithSession(
                        reservationDate,
                        reservationSession,
                        reservationStartTime.plusMinutes(1),
                        reservationEndTime.minusMinutes(1)
                );

        if (isThereAnotherReservationWithSameSession) {
            throw new ReservationException(ANOTHER_RESERVATION_ALREADY_EXISTS);
        }
    }

    private void validateReservationDateIsNotPast(LocalDate reservationDate) {
        if (reservationDate.isBefore(LocalDate.now())) {
            throw new ReservationException(INVALID_RESERVATION_DATE);
        }
    }

    // 예약 요청이 스케줄에 맞는 시간인지 검사
    private void validateReservationWithSchedule(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Optional<DailySchedule> dailyScheduleOpt = dailyScheduleRepository.findByDate(date);

        if (dailyScheduleOpt.isPresent()) {
            DailySchedule ds = dailyScheduleOpt.get();
            if (!ds.isActive()) {
                throw new ReservationException(RESERVATION_OUT_OF_SCHEDULE);
            }
            checkWithinSchedule(ds.getStartTime(), ds.getEndTime(), startTime, endTime);
            return;
        }

        WeeklySchedule ws = weeklyScheduleRepository.findByDayOfWeek(date.getDayOfWeek())
                .orElseThrow(() -> new ReservationException(RESERVATION_OUT_OF_SCHEDULE));

        if (!ws.isActive()) {
            throw new ReservationException(RESERVATION_OUT_OF_SCHEDULE);
        }

        checkWithinSchedule(ws.getStartTime(), ws.getEndTime(), startTime, endTime);
    }


    private void checkWithinSchedule(LocalTime availableStart, LocalTime availableEnd,
                                     LocalTime requestStart, LocalTime requestEnd) {
        if (requestStart.isBefore(availableStart) || requestEnd.isAfter(availableEnd)) {
            throw new ReservationException(RESERVATION_OUT_OF_SCHEDULE);
        }
    }
}
