package com.keunsori.keunsoriserver.domain.reservation.domain.validator;

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
import lombok.RequiredArgsConstructor;

import static com.keunsori.keunsoriserver.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    public void validateReservationCreateRequest(ReservationCreateRequest request) {
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
}