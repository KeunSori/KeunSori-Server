package com.keunsori.keunsoriserver.domain.reservation.service;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_NOT_EXISTS_WITH_ID;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.DailyAvailableResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.MonthlyScheduleResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;
    private final MemberUtil memberUtil;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;

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

    public MonthlyScheduleResponse findMonthlySchedule(String yearMonth) {
        List<DailyAvailableResponse> dailyAvailableResponses = findDailyAvailableByMonth(yearMonth);
        List<ReservationResponse> reservationResponses = findReservationsByMonth(yearMonth);
        return new MonthlyScheduleResponse(dailyAvailableResponses, reservationResponses);
    }

    private List<DailyAvailableResponse> findDailyAvailableByMonth(String yearMonth) {
        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(1);

        return Stream.iterate(start, date -> date.isBefore(end), date -> date.plusDays(1))
                .map(this::convertDateToDailyAvailableResponse).toList();
    }
    
    private DailyAvailableResponse convertDateToDailyAvailableResponse(LocalDate date) {
        return dailyScheduleRepository.findById(date)
                .map(DailyAvailableResponse::from)
                .orElseGet(() -> weeklyScheduleRepository.findByDayOfWeek(date.getDayOfWeek())
                        .map(schedule -> DailyAvailableResponse.of(date, schedule))
                        .orElseGet(() -> DailyAvailableResponse.createInactiveDate(date)));
    }

        return responses;
    }
}
