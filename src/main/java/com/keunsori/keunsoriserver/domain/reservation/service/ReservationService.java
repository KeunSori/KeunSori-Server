package com.keunsori.keunsoriserver.domain.reservation.service;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_COMPLETED;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_NOT_EQUAL_MEMBER;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_NOT_EXISTS_WITH_ID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.dto.response.ReservationResponse;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationUpdateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.ReservationException;
import com.keunsori.keunsoriserver.global.util.DateUtil;
import com.keunsori.keunsoriserver.global.util.MemberUtil;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberUtil memberUtil;

    public List<ReservationResponse> findReservationsByMonth(String yearMonth) {
        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(1);
        return reservationRepository.findAllByDateBetween(start, end)
                .stream().map(ReservationResponse::of).toList();
    }

    @Transactional
    public void createReservation(ReservationCreateRequest request) {
        Member member = memberUtil.getLoggedInMember();
        Reservation reservation = request.toEntity(member);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        Member member = memberUtil.getLoggedInMember();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS_WITH_ID));

        validateReservationDeletable(reservation, member);
        reservationRepository.delete(reservation);
    }

    @Transactional
    public void updateReservation(Long reservationId, ReservationUpdateRequest request) {
        Member member = memberUtil.getLoggedInMember();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS_WITH_ID));

        validateReservationUpdatable(reservation, member);
        reservation.updateReservation(
                request.reservationType(),
                request.reservationSession(),
                request.reservationDate(),
                request.reservationStartTime(),
                request.reservationEndTime()
        );
    }

    public List<ReservationResponse> findAllMyReservations() {
        Member member = memberUtil.getLoggedInMember();
        System.out.println("student id : " + member.getStudentId());
        return reservationRepository.findAllByMember(member)
                .stream().map(ReservationResponse::of).toList();
    }

    private void validateReservationDeletable(Reservation reservation, Member loggedInMember) {
        validateReservationNotComplete(reservation);
        if (loggedInMember.isAdmin()) {
            return;
        }
        validateReservationMember(reservation, loggedInMember);
    }

    private void validateReservationUpdatable(Reservation reservation, Member loggedInMember) {
        validateReservationMember(reservation, loggedInMember);
        validateReservationNotComplete(reservation);
    }

    private void validateReservationMember(Reservation reservation, Member loggedInMember) {
        if (!reservation.hasMember(loggedInMember)) {
            throw new ReservationException(RESERVATION_NOT_EQUAL_MEMBER);
        }
    }

    private void validateReservationNotComplete(Reservation reservation) {
        if (reservation.isComplete()) {
            throw new ReservationException(RESERVATION_COMPLETED);
        }
    }
}
