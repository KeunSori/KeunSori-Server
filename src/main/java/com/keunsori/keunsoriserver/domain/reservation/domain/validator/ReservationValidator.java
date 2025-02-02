package com.keunsori.keunsoriserver.domain.reservation.domain.validator;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.ANOTHER_RESERVATION_EXISTS;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_RESERVATION_TIME;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_ALREADY_COMPLETED;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_NOT_EQUAL_MEMBER;

import org.springframework.stereotype.Component;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationUpdateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.ReservationException;

import java.time.LocalTime;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    public void validateReservationCreateForm(ReservationCreateRequest request) {
        validateReservationTime(request.reservationStartTime(), request.reservationEndTime());

        boolean isThereAnotherReservation = reservationRepository.existsAnotherReservationAtDateAndTimePeriod(
                request.reservationDate(),
                request.reservationStartTime().plusMinutes(1),
                request.reservationEndTime().minusMinutes(1)
        );

        if (isThereAnotherReservation) {
            throw new ReservationException(ANOTHER_RESERVATION_EXISTS);
        }
    }

    public void validateReservationUpdateForm(ReservationUpdateRequest request) {
        validateReservationTime(request.reservationStartTime(), request.reservationEndTime());

        boolean isThereAnotherReservation = reservationRepository.existsAnotherReservationAtDateAndTimePeriod(
                request.reservationDate(),
                request.reservationStartTime().plusMinutes(1),
                request.reservationEndTime().minusMinutes(1)
        );

        if (isThereAnotherReservation) {
            throw new ReservationException(ANOTHER_RESERVATION_EXISTS);
        }
    }

    public void validateReservationDeletable(Reservation reservation, Member loggedInMember) {
        validateReservationNotComplete(reservation);
        if (loggedInMember.isAdmin()) {
            return;
        }
        validateReservationMember(reservation, loggedInMember);
    }

    public void validateReservationUpdatable(Reservation reservation, Member loggedInMember) {
        validateReservationMember(reservation, loggedInMember);
        validateReservationNotComplete(reservation);
    }

    private void validateReservationMember(Reservation reservation, Member loggedInMember) {
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
}
