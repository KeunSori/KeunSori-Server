package com.keunsori.keunsoriserver.domain.reservation.service;

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

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;
    private final MemberUtil memberUtil;

    public List<ReservationResponse> findReservationsByMonth(String yearMonth) {
        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(2);
        return reservationRepository.findAllByDateBetweenOrderByDateAscStartTimeAsc(start, end)
                .stream().map(ReservationResponse::from).toList();
    }

    @Transactional
    public Long createReservation(ReservationCreateRequest request) {
        Member member = memberUtil.getLoggedInMember();

        reservationValidator.validateReservationCreateRequest(request);
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
    public void deleteMyReservations(List<Long> reservationIds) {
        Member loginMember = memberUtil.getLoggedInMember();

        List<Reservation> reservations = reservationRepository.findAllById(reservationIds);

        if (reservations.size() != reservationIds.size()) {
            throw new ReservationException(RESERVATION_NOT_FOUND);
        }

        for (Reservation reservation : reservations) {

            // 정기예약 기반인 경우에만 팀장 본인 또는 관리자만 삭제 가능
            if (reservation.getRegularReservation() != null) {
                if (!reservation.hasMember(loginMember)) {
                    throw new ReservationException(RESERVATION_NOT_EQUALS_TEAM_LEADER);
                }
            }

            // 일반 개별 예약은 본인만 삭제 가능
            else {
                if (!reservation.hasMember(loginMember)) {
                    throw new ReservationException(RESERVATION_NOT_EQUAL_MEMBER);
                }
            }
        }

        reservationRepository.deleteAll(reservations);
    }

    @Transactional
    public void updateReservation(Long reservationId, ReservationUpdateRequest request) {
        Member member = memberUtil.getLoggedInMember();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS_WITH_ID));

        reservationValidator.validateReservationUpdateRequest(request);
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
        return reservationRepository.findAllByMemberOrderByDateDescStartTimeDesc(member)
                .stream().map(ReservationResponse::from).toList();
    }
}
