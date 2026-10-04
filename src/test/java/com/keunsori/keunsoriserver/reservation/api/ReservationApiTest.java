package com.keunsori.keunsoriserver.reservation.api;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.ANOTHER_RESERVATION_ALREADY_EXISTS;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_RESERVATION_TIME;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.RESERVATION_ALREADY_COMPLETED;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;

import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.DailySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.WeeklySchedule;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.DailyScheduleRepository;
import com.keunsori.keunsoriserver.domain.admin.reservation.repository.WeeklyScheduleRepository;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.DailyAvailableResponse;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

public class ReservationApiTest extends ApiTest {

    private String authorizationValue;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DailyScheduleRepository dailyScheduleRepository;

    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
        authorizationValue = "Bearer " + generalToken;
    }

    @Test
    void 내_예약_조회에_성공한다() {
        System.out.println(authorizationValue);
        given().
                header(AUTHORIZATION, authorizationValue).
        when().
                get("/reservation/my").
        then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 예약_가능_날짜는_일별_설정을_우선하고_주간_설정과_기본_비활성_값을_반환한다() throws JsonProcessingException {
        LocalDate dailyScheduleDate = LocalDate.of(2999, 1, 6);
        LocalDate weeklyScheduleDate = dailyScheduleDate.plusWeeks(1);
        LocalDate inactiveDate = dailyScheduleDate.plusDays(1);

        dailyScheduleRepository.save(DailySchedule.builder()
                .date(dailyScheduleDate)
                .isActive(false)
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(18, 0))
                .build());
        weeklyScheduleRepository.save(WeeklySchedule.builder()
                .dayOfWeek(dailyScheduleDate.getDayOfWeek())
                .isActive(true)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(22, 0))
                .build());

        String responseBody = given()
                .header(AUTHORIZATION, authorizationValue)
                .param("month", "299901")
                .when()
                .get("/reservation")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString();

        List<DailyAvailableResponse> responses = mapper.readValue(responseBody, new TypeReference<>() {});

        Assertions.assertThat(responses).hasSize(59);
        Assertions.assertThat(findSchedule(responses, dailyScheduleDate))
                .isEqualTo(new DailyAvailableResponse(dailyScheduleDate, false, "12:00", "18:00"));
        Assertions.assertThat(findSchedule(responses, weeklyScheduleDate))
                .isEqualTo(new DailyAvailableResponse(weeklyScheduleDate, true, "10:00", "22:00"));
        Assertions.assertThat(findSchedule(responses, inactiveDate))
                .isEqualTo(new DailyAvailableResponse(inactiveDate, false, "10:00", "23:00"));
    }

    private DailyAvailableResponse findSchedule(List<DailyAvailableResponse> responses, LocalDate date) {
        return responses.stream()
                .filter(response -> response.date().equals(date))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void 개인_예약_생성에_성공한다() throws JsonProcessingException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    void 팀_예약_생성에_성공한다() throws JsonProcessingException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "TEAM",
                "ALL",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    void 교습_예약_생성에_성공한다() throws JsonProcessingException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "LESSON",
                "ALL",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    void 종료시간이_시작시간보다_앞서는_경우_예약_생성에_실패한다() throws JsonProcessingException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(14, 0),
                LocalTime.of(12, 0)
        );

        String errorMessage =
            given().
                    header(AUTHORIZATION, authorizationValue).
                    header(CONTENT_TYPE, "application/json").
                    body(mapper.writeValueAsString(request)).
            when().
                    post("/reservation").
            then().
                    statusCode(HttpStatus.SC_BAD_REQUEST).
                    extract().
                    jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_RESERVATION_TIME);
    }

    @Test
    void 종료시간과_시작시간이_같은_경우_예약_생성에_실패한다() throws JsonProcessingException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(12, 0)
        );

        String errorMessage =
                given().
                        header(AUTHORIZATION, authorizationValue).
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request)).
                when().
                        post("/reservation").
                then().
                        statusCode(HttpStatus.SC_BAD_REQUEST).
                        extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_RESERVATION_TIME);
    }

    static Stream<Arguments> overlapReservationTimeTestData() {
        return Stream.of(
                Arguments.arguments(LocalTime.of(12, 0), LocalTime.of(14, 0)),
                Arguments.arguments(LocalTime.of(13, 0), LocalTime.of(14, 0)),
                Arguments.arguments(LocalTime.of(13, 0), LocalTime.of(15, 0)),
                Arguments.arguments(LocalTime.of(11, 0), LocalTime.of(13, 0)),
                Arguments.arguments(LocalTime.of(12, 30), LocalTime.of(13, 30))
        );
    }

    @ParameterizedTest
    @MethodSource("overlapReservationTimeTestData")
    void 세션이_같으면_예약_시간이_다른_예약과_겹칠_때_예약에_실패한다(LocalTime startTime, LocalTime endTime) throws JsonProcessingException {
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                startTime,
                endTime
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request1)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);

        String errorMessage =
        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request2)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                extract().
                jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(ANOTHER_RESERVATION_ALREADY_EXISTS);
    }

    @ParameterizedTest
    @MethodSource("overlapReservationTimeTestData")
    void 세션이_다르면_예약_시간이_겹쳐도_성공한다(LocalTime startTime, LocalTime endTime) throws JsonProcessingException {
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                "PERSONAL",
                "GUITAR",
                LocalDate.of(2999, 1, 1),
                startTime,
                endTime
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request1)).
                when().
                post("/reservation").
                then().
                statusCode(HttpStatus.SC_CREATED);

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request2)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);
    }

    @ParameterizedTest
    @MethodSource("overlapReservationTimeTestData")
    void 팀_예약이_존재하면_개인_예약의_예약_시간이_겹쳤을_때_실패한다(LocalTime startTime, LocalTime endTime) throws JsonProcessingException {
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                "TEAM",
                "ALL",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                "PERSONAL",
                "GUITAR",
                LocalDate.of(2999, 1, 1),
                startTime,
                endTime
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request1)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request2)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @MethodSource("overlapReservationTimeTestData")
    void 팀_예약은_예약_시간이_다른_예약과_겹칠_때_예약에_실패한다(LocalTime startTime, LocalTime endTime) throws JsonProcessingException {
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                "TEAM",
                "ALL",
                LocalDate.of(2999, 1, 1),
                startTime,
                endTime
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request1)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);

        String errorMessage =
                given().
                        header(AUTHORIZATION, authorizationValue).
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request2)).
                when().
                        post("/reservation").
                then().
                        statusCode(HttpStatus.SC_BAD_REQUEST).
                        extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(ANOTHER_RESERVATION_ALREADY_EXISTS);
    }

    @ParameterizedTest
    @MethodSource("overlapReservationTimeTestData")
    void 교습_예약이_존재하면_개인_예약의_예약_시간이_겹쳤을_때_실패한다(LocalTime startTime, LocalTime endTime) throws JsonProcessingException {
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                "LESSON",
                "ALL",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                "PERSONAL",
                "GUITAR",
                LocalDate.of(2999, 1, 1),
                startTime,
                endTime
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request1)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request2)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    static Stream<Arguments> successReservationTimeTestData() {
        return Stream.of(
                Arguments.arguments(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                Arguments.arguments(LocalTime.of(14, 0), LocalTime.of(15, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("successReservationTimeTestData")
    void 예약_시간이_경계에_걸쳐도_성공한다(LocalTime startTime, LocalTime endTime) throws JsonProcessingException {
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                startTime,
                endTime
        );

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request1)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request2)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    void 일반_유저가_자신의_예약_취소에_성공한다() throws JsonProcessingException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 31),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        String reservationLocation =
            given().
                    header(AUTHORIZATION, authorizationValue).
                    header(CONTENT_TYPE, "application/json").
                    body(mapper.writeValueAsString(request)).
            when().
                    post("/reservation").
            then().
                    statusCode(HttpStatus.SC_CREATED).
                    extract().
                    header(LOCATION);

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
        when().
                delete(reservationLocation).
        then().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void 예약_시간을_넘어간_예약은_확정되어_예약_취소에_실패한다() throws JsonProcessingException {
        Reservation pastReservation = Reservation.builder()
                .session(Session.ALL)
                .reservationType(ReservationType.TEAM)
                .date(LocalDate.of(2000, 1, 1))
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .build();
        reservationRepository.save(pastReservation);

        String errorMessage =
                given().
                        header(AUTHORIZATION, authorizationValue).
                when().
                        delete("/reservation/" + pastReservation.getId()).
                then().
                        statusCode(HttpStatus.SC_BAD_REQUEST).
                        extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(RESERVATION_ALREADY_COMPLETED);
    }
}
