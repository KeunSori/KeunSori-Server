package com.keunsori.keunsoriserver.admin.reservation.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.admin.init.ApiTestWithWeeklyScheduleInit;
import com.keunsori.keunsoriserver.admin.member.fixture.MemberFixture;
import com.keunsori.keunsoriserver.admin.reservation.fixture.RegularReservationFixture;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleManagementRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleUpdateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.RegularReservationRepository;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class AdminReservationApiTest extends ApiTestWithWeeklyScheduleInit {

    private String adminAuth;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RegularReservationRepository regularReservationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        login_with_admin_member();
        adminAuth = "Bearer " + adminToken;

        Member general = MemberFixture.GENERAL1();
        if (!memberRepository.existsByStudentId(general.getStudentId())) {
            memberRepository.save(general);
        }
    }

    @Test
    void 주간_예약_관리_페이지_정기_예약_포함_반환_성공() throws JsonProcessingException {
        DayOfWeek day = DayOfWeek.MONDAY;
        WeeklyScheduleUpdateRequest schedule = new WeeklyScheduleUpdateRequest(
                day.getValue() % 7,
                true,
                LocalTime.of(9,0),
                LocalTime.of(23,0)
        );

        RegularReservationCreateRequest regularReservation = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                day.toString(),
                "테스트팀",
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                "C000001",
                LocalDate.of(2999, 8, 11),
                LocalDate.of(2999, 9, 30)
        );

        WeeklyScheduleManagementRequest payload = new WeeklyScheduleManagementRequest(
                List.of(schedule),
                List.of(regularReservation),
                List.of()
        );

        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(payload))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_OK);

        var json = given()
                .header(AUTHORIZATION, adminAuth)
        .when()
                .get("/admin/reservation/weekly-schedule")
        .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath();

        List<?> mondayRegs = json.getList("find { it.dayOfWeekNum == 1 }.regularReservations");

        org.assertj.core.api.Assertions.assertThat(mondayRegs).isNotNull().isNotEmpty();
    }

    @Test
    void 주간_설정_성공() throws JsonProcessingException {
        List<WeeklyScheduleUpdateRequest> requests = new ArrayList<>();
        requests.add(new WeeklyScheduleUpdateRequest(1, true, LocalTime.parse("10:00"), LocalTime.parse("22:00"))); // 월요일 활성화

        WeeklyScheduleManagementRequest request = new WeeklyScheduleManagementRequest(
                requests,
                List.of(),
                List.of()
        );

        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .put("/admin/reservation/weekly-schedule/management")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 일자별_예약_관리_페이지_반환_성공() {
        given().
                header(AUTHORIZATION, adminAuth).
                param("month","202510").
                when().
                get("/admin/reservation/daily-schedule").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 일자별_시간_설정_성공() throws JsonProcessingException {
        DailyScheduleUpdateOrCreateRequest request = new DailyScheduleUpdateOrCreateRequest(LocalDate.now().plusDays(1),true, LocalTime.parse("10:00"), LocalTime.parse("20:00"));

        given().
                header(AUTHORIZATION, adminAuth).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                put("/admin/reservation/daily-schedule").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 종료시간이_시작시간보다_앞서는_경우_주간_설정_실패() throws JsonProcessingException {
        List<WeeklyScheduleUpdateRequest> schedules = List.of(
                new WeeklyScheduleUpdateRequest(1, true, LocalTime.of(20, 0), LocalTime.of(12, 0))
        );

        WeeklyScheduleManagementRequest request = new WeeklyScheduleManagementRequest(
                schedules, List.of(), List.of()
        );

        String errorMessage = given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .put("/admin/reservation/weekly-schedule/management")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .jsonPath().getString("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_SCHEDULE_TIME);
    }

    @Test
    void 종료시간이_시작시간과_같은_경우_주간_설정_실패() throws JsonProcessingException {
        List<WeeklyScheduleUpdateRequest> schedules = List.of(
                new WeeklyScheduleUpdateRequest(1, true, LocalTime.of(12, 0), LocalTime.of(12, 0))
        );

        WeeklyScheduleManagementRequest request = new WeeklyScheduleManagementRequest(
                schedules, List.of(), List.of()
        );

        String errorMessage = given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .put("/admin/reservation/weekly-schedule/management")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .jsonPath().getString("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_SCHEDULE_TIME);
    }

    @Test
    void 지나간_날짜는_일자별_설정에_실패한다() throws JsonProcessingException {
        DailyScheduleUpdateOrCreateRequest request = new DailyScheduleUpdateOrCreateRequest(LocalDate.of(2020,06,19),true, LocalTime.parse("10:00"), LocalTime.parse("20:00"));

        String errorMessage =
                given().
                        header(AUTHORIZATION, adminAuth).
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request)).
                        when().
                        put("/admin/reservation/daily-schedule").
                        then().
                        statusCode(HttpStatus.SC_BAD_REQUEST).
                        extract().
                        jsonPath().get("message");
        Assertions.assertThat(errorMessage).isEqualTo(INVALID_DATE_SCHEDULE);
    }
    @Test
    void 종료시간이_시작시간보다_앞서는_경우_일자별_설정에_실패한다() throws JsonProcessingException {
        DailyScheduleUpdateOrCreateRequest request = new DailyScheduleUpdateOrCreateRequest(LocalDate.now().plusDays(1),true, LocalTime.parse("20:00"), LocalTime.parse("10:00"));

        String errorMessage =
                given().
                        header(AUTHORIZATION, adminAuth).
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request)).
                        when().
                        put("/admin/reservation/daily-schedule").
                        then().
                        statusCode(HttpStatus.SC_BAD_REQUEST).
                        extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_SCHEDULE_TIME);
    }

    @Test
    void 주간_설정_시_범위_밖_예약은_삭제된다() throws JsonProcessingException {
        List<Reservation> reservationList1 = IntStream.range(1, 8)
                .mapToObj(i -> Reservation.builder()
                        .session(Session.ALL)
                        .reservationType(ReservationType.TEAM)
                        .date(LocalDate.now().plusDays(i))
                        .startTime(LocalTime.of(12, 0))
                        .endTime(LocalTime.of(14, 0))
                        .build())
                .toList();
        List<Reservation> reservationList2 = IntStream.range(1, 8)
                .mapToObj(i -> Reservation.builder()
                        .session(Session.ALL)
                        .reservationType(ReservationType.TEAM)
                        .date(LocalDate.now().plusDays(i))
                        .startTime(LocalTime.of(20, 0))
                        .endTime(LocalTime.of(22, 0))
                        .build())
                .toList();
        reservationRepository.saveAll(reservationList1);
        reservationRepository.saveAll(reservationList2);

        List<Reservation> beforeUpdateReservation = reservationRepository.findByDateGreaterThanEqual(LocalDate.now());
        Assertions.assertThat(beforeUpdateReservation).hasSize(14);

        List<WeeklyScheduleUpdateRequest> weeklyScheduleRequests = List.of(
                new WeeklyScheduleUpdateRequest(0, false, LocalTime.of(10, 0), LocalTime.of(23, 0)),
                new WeeklyScheduleUpdateRequest(1, true,  LocalTime.of(10, 0), LocalTime.of(20, 0)),
                new WeeklyScheduleUpdateRequest(2, false, LocalTime.of(10, 0), LocalTime.of(23, 0)),
                new WeeklyScheduleUpdateRequest(3, true, LocalTime.of(10, 0), LocalTime.of(21, 0)),
                new WeeklyScheduleUpdateRequest(4, false, LocalTime.of(10, 0), LocalTime.of(23, 0)),
                new WeeklyScheduleUpdateRequest(5, false, LocalTime.of(10, 0), LocalTime.of(23, 0)),
                new WeeklyScheduleUpdateRequest(6, true, LocalTime.of(12, 0), LocalTime.of(20, 0))
        );

        WeeklyScheduleManagementRequest request = new WeeklyScheduleManagementRequest(weeklyScheduleRequests, null, null);

        given().
                header(AUTHORIZATION, adminAuth).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                put("/admin/reservation/weekly-schedule/management").
                then().
                statusCode(HttpStatus.SC_OK);

        List<Reservation> remainedReservation = reservationRepository.findByDateGreaterThanEqual(LocalDate.now());
        Assertions.assertThat(remainedReservation).hasSize(3);
    }

    @Test
    void 일자별_시간_설정_시_범위_밖_예약은_삭제된다() throws JsonProcessingException {
        Reservation reservation1 = Reservation.builder()
                .session(Session.ALL)
                .reservationType(ReservationType.TEAM)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        reservationRepository.save(reservation1);

        Reservation reservation2 = Reservation.builder()
                .session(Session.ALL)
                .reservationType(ReservationType.TEAM)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .build();
        reservationRepository.save(reservation2);

        Reservation reservation3 = Reservation.builder()
                .session(Session.ALL)
                .reservationType(ReservationType.TEAM)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(20, 0))
                .endTime(LocalTime.of(21, 0))
                .build();
        reservationRepository.save(reservation3);

        DailyScheduleUpdateOrCreateRequest request = new DailyScheduleUpdateOrCreateRequest(LocalDate.now().plusDays(1),true, LocalTime.parse("12:00"), LocalTime.parse("20:00"));

        given().
                header(AUTHORIZATION, adminAuth).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                put("/admin/reservation/daily-schedule").
                then().
                statusCode(HttpStatus.SC_OK);

        List<Reservation> reservations = reservationRepository.findAllByDate(LocalDate.now().plusDays(1));

        Assertions.assertThat(reservations).allMatch(reservation ->
                reservation.getStartTime().isAfter(LocalTime.parse("11:59")) &&
                        reservation.getEndTime().isBefore(LocalTime.parse("20:01"))
        );
    }

    @Test
    void 관리자가_예약_삭제에_성공한다() {
        Reservation reservation = Reservation.builder()
                .session(Session.ALL)
                .reservationType(ReservationType.TEAM)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .build();
        reservationRepository.save(reservation);

        given().
                header(AUTHORIZATION, adminAuth).
                when().
                delete("/admin/reservation/" + reservation.getId()).
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void 지난_날짜의_예약은_관리자_예약_삭제에_실패한다(){
        Reservation reservation = Reservation.builder()
                .session(Session.ALL)
                .reservationType(ReservationType.TEAM)
                .date(LocalDate.of(2020,06,19))
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .build();
        reservationRepository.save(reservation);

        String errorMessage =
                given().
                        header(AUTHORIZATION, adminAuth).
                        when().
                        delete("/admin/reservation/" + reservation.getId()).
                        then().
                        statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract()
                        .jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(RESERVATION_ALREADY_COMPLETED);
    }

    @Test
    void 정기예약_생성_성공() throws JsonProcessingException {
        DayOfWeek day = DayOfWeek.MONDAY;

        WeeklyScheduleUpdateRequest schedule = new WeeklyScheduleUpdateRequest(
                day.getValue() % 7, true, LocalTime.of(9, 0), LocalTime.of(23, 0)
        );

        RegularReservationCreateRequest regularRequest = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                day.toString(),
                "테스트팀",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "C000001",
                LocalDate.of(2999, 8, 12),
                LocalDate.of(2999, 9, 30)
        );

        WeeklyScheduleManagementRequest request = new WeeklyScheduleManagementRequest(
                List.of(schedule),
                List.of(regularRequest),
                List.of()
        );

        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 정기예약_충돌_덮어쓰기_성공() throws JsonProcessingException {
        LocalDate date = LocalDate.of(2999, 8, 18);
        DayOfWeek day = date.getDayOfWeek();
        Member teamLeader = memberRepository.findByStudentId("C000001")
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        reservationRepository.saveAndFlush(Reservation.builder()
                .session(Session.ALL)
                .reservationType(ReservationType.TEAM)
                .date(date)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .member(teamLeader)
                .build());

        WeeklyScheduleUpdateRequest schedule = new WeeklyScheduleUpdateRequest(
                day.getValue() % 7, true, LocalTime.of(9, 0), LocalTime.of(23, 0)
        );

        RegularReservationCreateRequest request = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                day.toString(),
                "덮어쓰기예약",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "C000001",
                date,
                LocalDate.of(2999, 9, 30)
        );

        WeeklyScheduleManagementRequest payload = new WeeklyScheduleManagementRequest(
                List.of(schedule),
                List.of(request),
                List.of()
        );

        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(payload))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_OK);

        List<Reservation> reservations = reservationRepository.findAllByDate(date);
        Assertions.assertThat( reservations.stream().filter(r ->
                        r.getStartTime().equals(LocalTime.of(10, 0)) &&
                                r.getEndTime().equals(LocalTime.of(11, 0))
                ).count()
        ).isEqualTo(1L);
    }

    @Test
    void 하루짜리_정기_예약_생성_실패() throws JsonProcessingException {
        DayOfWeek day = DayOfWeek.MONDAY;
        WeeklyScheduleUpdateRequest schedule = new WeeklyScheduleUpdateRequest(
                day.getValue() % 7, true, LocalTime.of(9,0), LocalTime.of(23,0)
        );

        LocalDate date = LocalDate.of(2999, 8, 11);
        RegularReservationCreateRequest request = new RegularReservationCreateRequest(
                "TEAM",
                "ALL", day.toString(),
                "하루금지",
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                "C000001",
                date,
                date
        );

        WeeklyScheduleManagementRequest payload = new WeeklyScheduleManagementRequest(
                List.of(schedule),
                List.of(request),
                List.of()
        );

        String msg = given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(payload))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().jsonPath().getString("message");

        Assertions.assertThat(msg).isEqualTo(APPLY_DATE_SAME_WITH_END_DATE);
    }

    @Test
    void 정기_예약_삭제_및_저장_동시_성공() throws JsonProcessingException {
        DayOfWeek day = DayOfWeek.TUESDAY;
        WeeklyScheduleManagementRequest first = new WeeklyScheduleManagementRequest(
                List.of(new WeeklyScheduleUpdateRequest(day.getValue()%7, true, LocalTime.of(9,0), LocalTime.of(23,0))),
                List.of(new RegularReservationCreateRequest(
                        "TEAM",
                        "ALL",day.toString(),
                        "기존",
                        LocalTime.of(12,0),
                        LocalTime.of(13,0),
                        "C000001",
                        LocalDate.of(2999,8,1),
                        LocalDate.of(2999,9,30)
                )),
                List.of()
        );
        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE,"application/json")
                .body(mapper.writeValueAsString(first))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_OK);

        var json = given()
                .header(AUTHORIZATION, adminAuth)
        .when()
                .get("/admin/reservation/weekly-schedule")
        .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath();

        List<Integer> tuesdayIds = json.getList("find { it.dayOfWeekNum == 2 }.regularReservations.regularReservationId");
        Long deleteId = tuesdayIds.getFirst().longValue();

        // 3) 같은 요일/세션/적용기간, 시간이 겹치는 신규를 같은 요청에서 추가 + 기존 삭제
        WeeklyScheduleManagementRequest combined = new WeeklyScheduleManagementRequest(
                List.of(), // 스케줄 변경 없음
                List.of(new RegularReservationCreateRequest(
                        "TEAM",
                        "ALL",day.toString(),
                        "신규",
                        LocalTime.of(12,0),
                        LocalTime.of(12,30), // 기존(12-13)과 겹침
                        "C000001",
                        LocalDate.of(2999,8,1),
                        LocalDate.of(2999,9,30)
                )),
                List.of(deleteId)
        );

        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE,"application/json")
                .body(mapper.writeValueAsString(combined))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 정기_예약_조회시_과거의_정기_예약은_조회하지_않는다() throws JsonProcessingException {
        // given
        Member member = memberRepository.findByStudentId(MemberFixture.GENERAL1().getStudentId())
                .orElseGet(() -> memberRepository.save(MemberFixture.GENERAL1()));
        regularReservationRepository.save(RegularReservationFixture.PAST_REGULAR_RESERVATION_FOR_MONDAY(member));
        Long id1 = regularReservationRepository.save(RegularReservationFixture.TODAY_START_REGULAR_RESERVATION_FOR_MONDAY(member)).getId();
        Long id2 = regularReservationRepository.save(RegularReservationFixture.TODAY_END_REGULAR_RESERVATION_FOR_MONDAY(member)).getId();
        Long id3 = regularReservationRepository.save(RegularReservationFixture.FUTURE_REGULAR_RESERVATION_FOR_MONDAY(member)).getId();

        var responseJson =  given()
                                    .header(AUTHORIZATION, adminAuth)
                            .when()
                                    .get("/admin/reservation/weekly-schedule")
                            .then()
                                    .statusCode(HttpStatus.SC_OK)
                                    .extract()
                                    .jsonPath();

        List<Long> mondayIds = responseJson.getList("find { it.dayOfWeekNum == 1 }.regularReservations.regularReservationId", Long.class);

        Assertions.assertThat(mondayIds).containsOnly(id1, id2, id3);
    }

    @Test
    void 정기예약_시간수정_성공() throws JsonProcessingException {
        DayOfWeek day = DayOfWeek.MONDAY;

        WeeklyScheduleUpdateRequest schedule = new WeeklyScheduleUpdateRequest(
                day.getValue() % 7, true, LocalTime.of(9,0), LocalTime.of(23,0)
        );

        // today 기준으로 미래 기간
        LocalDate today = LocalDate.now();
        LocalDate applyStart = today.minusDays(3);
        LocalDate applyEnd = today.plusWeeks(3);

        RegularReservationCreateRequest createRequest = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                day.toString(),
                "시간수정테스트팀",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "C000001",
                applyStart,
                applyEnd
        );

        WeeklyScheduleManagementRequest payload = new WeeklyScheduleManagementRequest(
                List.of(schedule),
                List.of(createRequest),
                List.of()
        );

        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE,"application/json")
                .body(mapper.writeValueAsString(payload))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_OK);

        RegularReservation rr = regularReservationRepository.findAllAppliedFromToday().stream()
                .filter(r -> r.getDayOfWeek() == day)
                .filter(r -> r.getStartTime().equals(LocalTime.of(10, 0)) && r.getEndTime().equals(LocalTime.of(11, 0)))
                .findFirst()
                .orElseThrow();

        Long rrId = rr.getId();

        List<Reservation> before = reservationRepository.findByDateGreaterThanEqual(today).stream()
                .filter(r -> r.getRegularReservation() != null && r.getRegularReservation().getId().equals(rrId))
                .toList();
        Assertions.assertThat(before).isNotEmpty();
        Assertions.assertThat(before).allMatch(r ->
                r.getStartTime().equals(LocalTime.of(10, 0)) && r.getEndTime().equals(LocalTime.of(11, 0))
        );

        // when: 정기예약 시간 수정 API 호출
        var updateReq = List.of(new com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.RegularReservationUpdateRequest(
                rrId,
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
        ));
        given()
                .header(AUTHORIZATION, adminAuth)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(updateReq))
        .when()
                .put("/admin/reservation/regular-reservations/time")
                .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);

        RegularReservation updated = regularReservationRepository.findById(rrId).orElseThrow();
        Assertions.assertThat(updated.getStartTime()).isEqualTo(LocalTime.of(12, 0));
        Assertions.assertThat(updated.getEndTime()).isEqualTo(LocalTime.of(13, 0));

        List<Reservation> after = reservationRepository.findByDateGreaterThanEqual(today).stream()
                .filter(r -> r.getRegularReservation() != null && r.getRegularReservation().getId().equals(rrId))
                .toList();

        Assertions.assertThat(after).isNotEmpty();
        Assertions.assertThat(after).allMatch(r ->
                r.getStartTime().equals(LocalTime.of(12, 0)) && r.getEndTime().equals(LocalTime.of(13, 0))
        );
    }
}
