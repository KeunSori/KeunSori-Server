package com.keunsori.keunsoriserver.domain.reservation.service;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_NOT_EXISTS_WITH_ID;

import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.validator.ReservationValidator;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
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
    private final ReservationValidator reservationValidator;
    private final MemberUtil memberUtil;

    public List<ReservationResponse> findReservationsByMonth(String yearMonth) {
        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(1);
        return reservationRepository.findAllByDateBetween(start, end)
                .stream().map(ReservationResponse::from).toList();
    }

    @Transactional
    public Long createReservation(ReservationCreateRequest request) {
        Member member = memberUtil.getLoggedInMember();

        reservationValidator.validateReservationFromCreateRequest(request);
        Reservation reservation = request.toEntity(member);

        reservationRepository.save(reservation);
        return reservation.getId();
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        Member member = memberUtil.getLoggedInMember();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS_WITH_ID));

        reservationValidator.validateReservationDeletable(reservation, member);
        reservationRepository.delete(reservation);
    }

    @Transactional
    public void updateReservation(Long reservationId, ReservationUpdateRequest request) {
        Member member = memberUtil.getLoggedInMember();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS_WITH_ID));

        reservationValidator.validateReservationFromUpdateRequest(request);
        reservationValidator.validateOriginalReservationUpdatable(reservation, member);

        reservation.updateReservation(
                ReservationType.from(request.reservationSession()),
                Session.from(request.reservationSession()),
                request.reservationDate(),
                request.reservationStartTime(),
                request.reservationEndTime()
        );
    }

    public List<ReservationResponse> findAllMyReservations() {
        Member member = memberUtil.getLoggedInMember();
        return reservationRepository.findAllByMember(member)
                .stream().map(ReservationResponse::from).toList();
    }
}
