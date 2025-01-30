package com.keunsori.keunsoriserver.reservation.api;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_RESERVATION_TIME;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;


public class ReservationApiTest extends ApiTest {

    private String authorizationValue;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
        authorizationValue = "Bearer " + token;
    }

    @Test
    void 내_예약_다건_조회_성공() {
        given().
                header(AUTHORIZATION, authorizationValue).
        when().
                get("/reservation/my").
        then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 예약_생성에_성공한다() throws JsonProcessingException {
        ReservationCreateRequest request = new ReservationCreateRequest(
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
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
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
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

    static Stream<Arguments> failedReservationTimeTestData() {
        return Stream.of(
                Arguments.arguments(LocalTime.of(12, 0), LocalTime.of(14, 0)),
                Arguments.arguments(LocalTime.of(13, 0), LocalTime.of(14, 0)),
                Arguments.arguments(LocalTime.of(11, 0), LocalTime.of(13, 0)),
                Arguments.arguments(LocalTime.of(12, 30), LocalTime.of(13, 30))
        );
    }

    @ParameterizedTest
    @MethodSource("failedReservationTimeTestData")
    void 예약_시간이_다른_예약과_겹치면_예약에_실패한다(LocalTime startTime, LocalTime endTime) throws JsonProcessingException {
        ReservationCreateRequest request1 = new ReservationCreateRequest(
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
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
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        ReservationCreateRequest request2 = new ReservationCreateRequest(
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
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
}
