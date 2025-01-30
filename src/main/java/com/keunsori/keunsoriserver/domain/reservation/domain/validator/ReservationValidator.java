package com.keunsori.keunsoriserver.domain.reservation.domain.validator;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.ANOTHER_RESERVATION_EXISTS;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_RESERVATION_TIME;

import org.springframework.stereotype.Component;

import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.ReservationException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    public void validateReservationCreation(ReservationCreateRequest request) {
        if (!request.reservationEndTime().isAfter(request.reservationStartTime())) {
            throw new ReservationException(INVALID_RESERVATION_TIME);
        }

        boolean isThereAnotherReservation = reservationRepository.existsAnotherReservationAtDateAndTimePeriod(
                request.reservationDate(),
                request.reservationStartTime().plusMinutes(1),
                request.reservationEndTime().minusMinutes(1)
        );

        if (isThereAnotherReservation) {
            throw new ReservationException(ANOTHER_RESERVATION_EXISTS);
        }
    }

}
