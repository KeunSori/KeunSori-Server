package com.keunsori.keunsoriserver.domain.admin.reservation.service;

import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.RegularReservationResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.validator.RegularReservationValidator;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleManagementRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleUpdateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.DailyAvailableResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleManagementResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.RegularReservationRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.validator.ReservationValidator;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.exception.ReservationException;
import com.keunsori.keunsoriserver.global.util.DateUtil;
import com.keunsori.keunsoriserver.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public WeeklyScheduleManagementResponse saveWeeklyScheduleAndRegularReservations(WeeklyScheduleManagementRequest request, boolean force) {
        boolean hasScheduleChanged = request.weeklyScheduleUpdateRequestList() != null
                && !request.weeklyScheduleUpdateRequestList().isEmpty();
        boolean hasCreated = request.regularReservationCreateRequestList() != null
                && !request.regularReservationCreateRequestList().isEmpty();
        boolean hasDeleted = request.deleteRegularReservationIds() != null
                && !request.deleteRegularReservationIds().isEmpty();

        List<RegularReservationResponse> createdRegularReservations = Collections.emptyList();
        List<Long> deletedRegularReservationIds = Collections.emptyList();

        // 주간 스케줄 저장
        if(hasScheduleChanged){
            saveWeeklySchedule(request.weeklyScheduleUpdateRequestList());
        }

        // 정기 예약 생성
        if(hasCreated) {
            createdRegularReservations = createRegularReservations(request.regularReservationCreateRequestList(), force);
        }

        // 정기 예약 삭제
        if(hasDeleted) {
            deleteRegularReservations(request.deleteRegularReservationIds());
            deletedRegularReservationIds = request.deleteRegularReservationIds();
        }


        StringBuilder messageBuilder = new StringBuilder();

        if(hasScheduleChanged){
            messageBuilder.append("주간 스케줄이 변경되었습니다.");
        }

        if(hasCreated && hasDeleted) {
            messageBuilder.append("정기 예약이 생성 및 삭제되었습니다.");
        } else if (hasCreated) {
            messageBuilder.append("정기 예약이 생성되었습니다.");
        } else if (hasDeleted) {
            messageBuilder.append("정기 예약이 삭제되었습니다.");
        }

        if(messageBuilder.length() == 0){
            messageBuilder.append("변경된 내용이 없습니다.");
        }

        return new WeeklyScheduleManagementResponse(
                messageBuilder.toString(),
                createdRegularReservations,
                deletedRegularReservationIds
        );
    }

    // 주간 스케줄 조회
    public List<WeeklyScheduleResponse> findAllWeeklySchedules() {
        return Arrays.stream(DayOfWeek.values())
                .map(day -> weeklyScheduleRepository.findByDayOfWeek(day)
                        .map(WeeklyScheduleResponse::from)
                        .orElseGet(() -> WeeklyScheduleResponse.createInactiveDay(day)))
                .sorted(Comparator.comparing(WeeklyScheduleResponse::dayOfWeekNum))
                .collect(Collectors.toList());
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
            throw new ReservationException(RESERVATION_NOT_FOUND);
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
    public List<RegularReservationResponse> createRegularReservations(List<RegularReservationCreateRequest> requests, boolean force){
        List<RegularReservationResponse> responses = new ArrayList<>();

        for (RegularReservationCreateRequest regularReservationCreateRequest :requests) {
            // 학번으로 멤버 조회
            Member teamLeader = memberRepository.findByStudentId(regularReservationCreateRequest.studentId())
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

            // 유효성 검증
            regularReservationValidator.validateCreateRegularReservation(regularReservationCreateRequest);

            // 정기 예약 저장
            RegularReservation savedRegularReservation = regularReservationRepository.save(regularReservationCreateRequest.toEntity(teamLeader));

            // 기존 예약과의 충돌 여부 확인
            List<LocalDate> conflicts = findConflictingDates(savedRegularReservation);

            //
            if(!conflicts.isEmpty() && !force){
                throw new ReservationException(ANOTHER_RESERVATION_ALREADY_EXISTS);
            }

            // 기존 예약 삭제하고 정기 예약에 따른 일간 예약 강제 생성
            if(!conflicts.isEmpty() && force){
                reservationRepository.deleteAllByDateInAndTimeRange(
                        conflicts,
                        savedRegularReservation.getStartTime(),
                        savedRegularReservation.getEndTime()
                );
            }

            // 일간 예약 생성
            generateDailyReservations(savedRegularReservation);

            responses.add(RegularReservationResponse.from(savedRegularReservation));
        }
        return responses;
    }

    // 정기 예약 삭제
    @Transactional
    public void deleteRegularReservations(List<Long> ids){
        Member loginMember = memberUtil.getLoggedInMember();
        regularReservationValidator.validateDeletable(loginMember);

        List<RegularReservation> regularReservations = regularReservationRepository.findAllById(ids);
        regularReservationValidator.validateAndGetAllExists(ids, regularReservations);

        for (RegularReservation regularReservation : regularReservations) {
            // 연결된 일간 예약 같이 삭제
            reservationRepository.deleteAllByRegularReservation(regularReservation);
        }
        regularReservationRepository.deleteAll(regularReservations);
    }

    // 정기 예약과 겹치는 기존 일간 예약 목록 반환
    private List<LocalDate> findConflictingDates(RegularReservation savedRegularReservation) {
        List<LocalDate> conflicts = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate reservationDate = savedRegularReservation.getApplyStartDate();

        // 시작 날짜 오늘로 조정
        if(reservationDate.isBefore(today)){
            reservationDate = today;
        }

        // 요일 맞추기
        while (!reservationDate.getDayOfWeek().equals(savedRegularReservation.getDayOfWeek())) {
            reservationDate = reservationDate.plusDays(1);
        }

        // 종료일까지 반복
        while (!reservationDate.isAfter(savedRegularReservation.getApplyEndDate())) {
            boolean exists = reservationRepository.existsAnotherReservationAtDateAndTimePeriod(
                    reservationDate,
                    savedRegularReservation.getStartTime(),
                    savedRegularReservation.getEndTime()
            );
            if (exists) {
                conflicts.add(reservationDate);
            }
            reservationDate = reservationDate.plusDays(7);
        }
        return conflicts;
    }

    // 정기 예약으로부터 일간 예약 생성
    private void generateDailyReservations(RegularReservation savedRegularReservation) {
        LocalDate today = LocalDate.now();
        LocalDate reservationDate = savedRegularReservation.getApplyStartDate();

        // 시작 날짜를 오늘로 조정
        if(reservationDate.isBefore(today)){
            reservationDate = today;
        }

        while(!reservationDate.getDayOfWeek().equals(savedRegularReservation.getDayOfWeek())){
            reservationDate = reservationDate.plusDays(1);
        }

        // 이후 7일씩 반복
        while(!reservationDate.isAfter(savedRegularReservation.getApplyEndDate())){
            ReservationCreateRequest dto = new ReservationCreateRequest(
                    savedRegularReservation.getReservationType().name(),
                    savedRegularReservation.getSession().name(),
                    reservationDate,
                    savedRegularReservation.getStartTime(),
                    savedRegularReservation.getEndTime()
            );

            reservationValidator.validateReservationCreateRequest(dto);

            Reservation reservation = Reservation.builder()
                    .reservationType(savedRegularReservation.getReservationType())
                    .session(savedRegularReservation.getSession())
                    .date(reservationDate)
                    .startTime(savedRegularReservation.getStartTime())
                    .endTime(savedRegularReservation.getEndTime())
                    .member(savedRegularReservation.getMember())
                    .regularReservation(savedRegularReservation)
                    .build();

            reservationRepository.save(reservation);

            reservationDate = reservationDate.plusDays(7);
        }
    }

    // 요일별 정기 예약 조회
    public List<RegularReservationResponse> findRegularReservationsByDay(DayOfWeek dayOfWeek) {
        Member loginMember = memberUtil.getLoggedInMember();

        regularReservationValidator.validateAdminOrThrow(loginMember);

        return regularReservationRepository.findAllByDayOfWeekOrderByStartTime(dayOfWeek)
                .stream()
                .map(RegularReservationResponse::from)
                .toList();
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
