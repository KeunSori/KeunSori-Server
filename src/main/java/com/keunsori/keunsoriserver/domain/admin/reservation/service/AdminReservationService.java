package com.keunsori.keunsoriserver.domain.admin.reservation.service;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.RegularReservationResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.validator.RegularReservationValidator;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleManagementRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleUpdateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.DailyAvailableResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.RegularReservationRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.validator.ReservationValidator;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.exception.ReservationException;
import com.keunsori.keunsoriserver.global.util.DateUtil;
import com.keunsori.keunsoriserver.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminReservationService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;
    private final RegularReservationValidator regularReservationValidator;
    private final MemberRepository memberRepository;
    private final RegularReservationRepository regularReservationRepository;
    private final MemberUtil memberUtil;

    // 주간 스케줄 + 정기 예약 통합 저장/수정/삭제 메서드
    @Transactional
    public void saveWeeklyScheduleAndRegularReservations(WeeklyScheduleManagementRequest request) {
        boolean hasScheduleChanged = !CollectionUtils.isEmpty(request.weeklyScheduleUpdateRequestList());
        boolean hasCreated = !CollectionUtils.isEmpty(request.regularReservationCreateRequestList());
        boolean hasDeleted = !CollectionUtils.isEmpty(request.deleteRegularReservationIds());

        if(!hasScheduleChanged && !hasCreated && !hasDeleted) {
            throw new ReservationException(EMPTY_MANAGEMENT_REQUEST);
        }

        if(hasScheduleChanged) {
            saveWeeklySchedule(request.weeklyScheduleUpdateRequestList());
        }

        if(hasCreated) {
            createRegularReservations(request.regularReservationCreateRequestList());
        }

        if(hasDeleted) {
            deleteRegularReservations(request.deleteRegularReservationIds());
        }

    }

    // 주간 스케줄 조회 + 요일별 정기 예약 조회
    public List<WeeklyScheduleResponse> findAllWeeklySchedules() {
        Member loginMember = memberUtil.getLoggedInMember();
        loginMember.validateAdmin();

        // 정기예약 전체 조회 후 요일별 그룹핑
        Map<DayOfWeek, List<RegularReservationResponse>> regsByDay = regularReservationRepository.findAllByOrderByDayOfWeekAscStartTimeAsc()
                .stream()
                .map(RegularReservationResponse::from)
                .collect(Collectors.groupingBy(
                        RegularReservationResponse::dayOfWeek,
                        () -> new EnumMap<>(DayOfWeek.class),
                        Collectors.toList())
                );

        Map<DayOfWeek, WeeklySchedule> wsByDay = weeklyScheduleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        WeeklySchedule::getDayOfWeek,
                        ws -> ws,
                        (a,b) -> a,
                        () -> new EnumMap<>(DayOfWeek.class)
                ));

        return Arrays.stream(DayOfWeek.values())
                .map(day ->{
                    List<RegularReservationResponse> regs = regsByDay.getOrDefault(day, List.of());
                    WeeklySchedule ws = wsByDay.get(day);
                    if (ws != null) {
                        return WeeklyScheduleResponse.from(ws, regs);
                    }
                    return WeeklyScheduleResponse.createInactiveDay(day, regs);
                })
                .sorted(Comparator.comparing(WeeklyScheduleResponse::dayOfWeekNum))
                .toList();
    }


    // 주간 스케줄 저장
    @Transactional
    public void saveWeeklySchedule(List<WeeklyScheduleUpdateRequest> requests) {
        requests.stream()
                .peek(request -> validateScheduleTime(request.startTime(), request.endTime()))
                .map(WeeklyScheduleUpdateRequest::toEntity)
                .forEach(weeklyScheduleRepository::save);
    }

    // 일간 스케줄 저장
    @Transactional
    public void saveDailySchedule(DailyScheduleUpdateOrCreateRequest request) {
        DailySchedule dailySchedule = DailySchedule.builder()
                .date(request.date())
                .isActive(request.isActive())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        validateNotPastDateSchedule(dailySchedule);
        validateScheduleTime(dailySchedule.getStartTime(),dailySchedule.getEndTime());

        if(dailySchedule.isActive()){
            // active이면 설정된 시간 범위 밖 예약들 삭제
            List<Reservation> reservationsToDelete = reservationRepository.findAllByDate(dailySchedule.getDate()).stream()
                    .filter(reservation -> reservation.isValidTimeFor(dailySchedule)).toList();
            reservationRepository.deleteAll(reservationsToDelete);
        } else {
            // unactive 시 예약들 삭제
            reservationRepository.deleteAllByDate(dailySchedule.getDate());
        }

        dailyScheduleRepository.save(dailySchedule);
    }

    @Transactional
    public void deleteReservationByAdmin(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(RESERVATION_NOT_EXISTS_WITH_ID));

        // 지난 예약인지 확인
        reservationValidator.validateReservationNotComplete(reservation);

        reservationRepository.delete(reservation);
    }

    @Transactional
    public void deleteReservationsByAdmin(List<Long> reservationIds) {
        List<Reservation> reservations = reservationRepository.findAllById(reservationIds);

        if (reservations.size() != reservationIds.size()) {
            throw new ReservationException(PARTIAL_RESERVATION_NOT_FOUND);
        }

        reservationRepository.deleteAll(reservations);
    }

    public List<DailyAvailableResponse> findDailyAvailableByMonth(String yearMonth) {

        LocalDate start = DateUtil.parseMonthToFirstDate(yearMonth);
        LocalDate end = start.plusMonths(2);

        return Stream.iterate(start, date -> date.isBefore(end), date -> date.plusDays(1))
                .map(this::convertDateToDailyAvailableResponse).toList();
    }

    private DailyAvailableResponse convertDateToDailyAvailableResponse(LocalDate date) {
        return dailyScheduleRepository.findByDate(date)
                .map(DailyAvailableResponse::from)
                .orElseGet(() -> weeklyScheduleRepository.findByDayOfWeek(date.getDayOfWeek())
                        .map(schedule -> DailyAvailableResponse.of(date, schedule))
                        .orElseGet(() -> DailyAvailableResponse.createInactiveDate(date)));
    }

    // 정기 예약 생성
    @Transactional
    public void createRegularReservations(List<RegularReservationCreateRequest> requests){
        for (RegularReservationCreateRequest regularReservationCreateRequest :requests) {
            // 학번으로 멤버 조회
            Member teamLeader = memberRepository.findByStudentId(regularReservationCreateRequest.studentId())
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

            // 유효성 검증
            regularReservationValidator.validateCreateRegularReservation(regularReservationCreateRequest);

            // 정기 예약 저장
            RegularReservation savedRegularReservation = regularReservationRepository.save(regularReservationCreateRequest.toEntity(teamLeader));

            generateDailyReservationsOverwrite(savedRegularReservation);
        }
    }

    // 정기 예약 삭제
    @Transactional
    public void deleteRegularReservations(List<Long> ids){
        Member loginMember = memberUtil.getLoggedInMember();
        loginMember.validateAdmin();

        List<RegularReservation> regularReservations = regularReservationRepository.findAllById(ids);
        regularReservationValidator.validateAllIdExists(ids, regularReservations);

        for (RegularReservation regularReservation : regularReservations) {
            // 연결된 일간 예약 같이 삭제
            reservationRepository.deleteAllByRegularReservation(regularReservation);
        }
        regularReservationRepository.deleteAll(regularReservations);
    }

    // 정기 예약으로부터 일간 예약 생성
    private void generateDailyReservationsOverwrite(RegularReservation savedRegularReservation) {
        LocalDate start = savedRegularReservation.getApplyStartDate();
        LocalDate end = savedRegularReservation.getApplyEndDate();
        LocalDate today = LocalDate.now();

        if (end.isBefore(today)) {return;}
        if (start.isBefore(today)) {
            start = today;
        }

        LocalDate d = start;
        while (d.getDayOfWeek() != savedRegularReservation.getDayOfWeek()) d = d.plusDays(1);
        if (d.isAfter(end)) return;

        List<Reservation> news = new ArrayList<>();
        for (; !d.isAfter(end); d = d.plusWeeks(1)) {
            reservationRepository.deleteOverlapping(
                    d,
                    savedRegularReservation.getSession(),
                    savedRegularReservation.getStartTime(),
                    savedRegularReservation.getEndTime());
            news.add(Reservation.builder()
                    .reservationType(savedRegularReservation.getReservationType())
                    .session(savedRegularReservation.getSession())
                    .date(d)
                    .startTime(savedRegularReservation.getStartTime())
                    .endTime(savedRegularReservation.getEndTime())
                    .member(savedRegularReservation.getMember())
                    .regularReservation(savedRegularReservation)
                    .build());
        }
        reservationRepository.saveAll(news);
    }


    // 검증 메서드
    private void validateNotPastDateSchedule(DailySchedule schedule){
        if(schedule.isPastDate()) {
            throw new ReservationException(INVALID_DATE_SCHEDULE);
        }
    }

    private void validateScheduleTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new ReservationException(INVALID_SCHEDULE_TIME);
        }
    }
}
