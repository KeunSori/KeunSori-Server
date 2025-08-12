package com.keunsori.keunsoriserver.admin.reservation.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.admin.init.ApiTestWithWeeklyScheduleInit;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleManagementRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleUpdateRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.vo.Session;
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
import java.util.ArrayList;
import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class AdminReservationApiTest extends ApiTestWithWeeklyScheduleInit {
    private String authorizationValue;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        login_with_admin_member();
        authorizationValue = "Bearer " + adminToken;

        if (!memberRepository.existsByStudentId("C000001")) {
            memberRepository.save(Member.builder()
                    .studentId("C000001")
                    .email("test@example.com")
                    .password(passwordEncoder.encode("test123!"))
                    .status(MemberStatus.일반)
                    .build());
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
                .header(AUTHORIZATION, authorizationValue)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(payload))
        .when()
                .put("/admin/reservation/weekly-schedule/management")
        .then()
                .statusCode(HttpStatus.SC_OK);

        var json = given()
                .header(AUTHORIZATION, authorizationValue)
        .when()
                .get("/admin/reservation/weekly-schedule")
        .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath();

        List<List<?>> mondayRegsList = json.getList("$[?(@.dayOfWeekNum == 1)].regularReservations");
        org.assertj.core.api.Assertions.assertThat(mondayRegsList).isNotNull().isNotEmpty();
        org.assertj.core.api.Assertions.assertThat(mondayRegsList.get(0)).isInstanceOf(List.class);
        org.assertj.core.api.Assertions.assertThat((List<?>) mondayRegsList.get(0)).isNotEmpty();
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
                .header(AUTHORIZATION, authorizationValue)
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
                header(AUTHORIZATION, authorizationValue).
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
                header(AUTHORIZATION, authorizationValue).
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
                .header(AUTHORIZATION, authorizationValue)
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
                .header(AUTHORIZATION, authorizationValue)
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
                        header(AUTHORIZATION, authorizationValue).
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
                        header(AUTHORIZATION, authorizationValue).
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
                header(AUTHORIZATION, authorizationValue).
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
                header(AUTHORIZATION, authorizationValue).
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
                        header(AUTHORIZATION, authorizationValue).
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
                .header(AUTHORIZATION, authorizationValue)
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
                .header(AUTHORIZATION, authorizationValue)
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
}
