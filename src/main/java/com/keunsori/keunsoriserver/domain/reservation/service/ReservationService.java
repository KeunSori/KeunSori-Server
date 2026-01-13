package com.keunsori.keunsoriserver.domain.reservation.service;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.reservation.dto.response.DailyUnavailableSlotsResponse;
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
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;
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
            throw new ReservationException(PARTIAL_RESERVATION_NOT_FOUND);
        }

        for (Reservation reservation : reservations) {

            if (!reservation.hasMember(loginMember)) {
                if (reservation.getRegularReservation() != null) {
                    throw new ReservationException(RESERVATION_NOT_EQUALS_TEAM_LEADER);
                }
                throw new ReservationException(RESERVATION_NOT_EQUAL_MEMBER);
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

    // 예약 가능한 시간 테이블 반환
    public List<DailyUnavailableSlotsResponse> findDailyUnavailableSlotsForTwoMonths(String yearMonth) {

        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(2);

        return Stream.iterate(start, date -> date.isBefore(end), date -> date.plusDays(1))
                .map(this::convertDateToDailyUnavailableSlotsResponse).toList();
    }

    // 하루의 예약 불가능 시간 슬롯 Dto 반환
    private DailyUnavailableSlotsResponse convertDateToDailyUnavailableSlotsResponse(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findAllByDate(date);

        // 일간 스케줄이 있을 경우 계산, 없을 경우 주간 스케줄로 계산
        return dailyScheduleRepository.findByDate(date)
                .map(dailySchedule -> {
                    List<Integer> unavailableSlots = generateUnavailableSlots(
                            dailySchedule.getStartTime(),
                            dailySchedule.getEndTime(),
                            reservations
                    );
                    return DailyUnavailableSlotsResponse.of(date, dailySchedule.isActive(), unavailableSlots);
                })
                .orElseGet(() -> {
                    WeeklySchedule weeklySchedule = weeklyScheduleRepository.findByDayOfWeek(date.getDayOfWeek())
                            .orElseThrow(() -> new ReservationException(WEEKLY_SCHEDULE_NOT_FOUND));
                    List<Integer> unavailableSlots = generateUnavailableSlots(
                            weeklySchedule.getStartTime(),
                            weeklySchedule.getEndTime(),
                            reservations
                    );
                    return DailyUnavailableSlotsResponse.of(date, weeklySchedule.isActive(), unavailableSlots);
                });
    }

    private List<Integer> generateUnavailableSlots(LocalTime start, LocalTime end, List<Reservation> reservations) {
        Set<Integer> unavailable = new HashSet<>();
        int scheduleStartIndex = toStartIndex(start);
        int scheduleEndIndex = toEndIndex(end);

        for (int i = 0; i < scheduleStartIndex; i++) {
            unavailable.add(i);
        }
        for (int i = scheduleEndIndex; i < 48; i++) {
            unavailable.add(i);
        }

        for (Reservation reservation : reservations) {
            int startIndex = toStartIndex(reservation.getStartTime());
            int endIndex = toEndIndex(reservation.getEndTime());
            for (int i = startIndex; i < endIndex; i++) {
                if (i >= 0 && i < 48) {
                    unavailable.add(i);
                }
            }
        }

        List<Integer> result = new ArrayList<>(unavailable);
        Collections.sort(result);
        return result;
    }

    private int toStartIndex(LocalTime time) {
        return time.getHour() * 2 + (time.getMinute() >= 30 ? 1 : 0);
    }

    private int toEndIndex(LocalTime time) {
        return time.getHour() * 2 + (time.getMinute() > 0 ? (time.getMinute() > 30 ? 2 : 1) : 0);
    }

}
