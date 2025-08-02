package com.keunsori.keunsoriserver.admin.regularreservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.admin.init.ApiTestWithWeeklyScheduleInit;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.ANOTHER_REGULAR_RESERVATION_ALREADY_EXISTS;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REGULAR_RESERVATION_TIME;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class RegularReservationApiTest extends ApiTestWithWeeklyScheduleInit {

    private String authorizationValue;

    @Autowired
    MemberRepository memberRepository;

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
    void 정기_예약_전체_조회_성공() {
        given()
                .header(AUTHORIZATION, authorizationValue)
        .when()
                .get("/admin/regular-reservation")
        .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 정기_예약_생성_성공() throws JsonProcessingException {
        RegularReservationCreateRequest regularReservationCreateRequest = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                DayOfWeek.FRIDAY,
                "사무라이 하트",
                LocalTime.of(18, 30),
                LocalTime.of(20,0),
                "C000001",
                LocalDate.now(),
                LocalDate.now().plusWeeks(2)
        );

        given()
                .header(AUTHORIZATION, authorizationValue)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(regularReservationCreateRequest))
        .when()
                .post("/admin/regular-reservation")
        .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 시작_시간이_종료_시간보다_늦으면_정기_예약_생성_실패() throws JsonProcessingException {
        RegularReservationCreateRequest regularReservationCreateRequest = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                DayOfWeek.MONDAY,
                "오류 테스트 팀",
                LocalTime.of(19,0),
                LocalTime.of(18,0),
                "C000001",
                LocalDate.now(),
                LocalDate.now().plusWeeks(1)
        );

        String errorMessage =
                given()
                        .header(AUTHORIZATION, authorizationValue)
                        .header(CONTENT_TYPE, "application/json")
                        .body(mapper.writeValueAsString(regularReservationCreateRequest))
                .when()
                        .post("/admin/regular-reservation")
                .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract().jsonPath().get("message");

        assertThat(errorMessage).isEqualTo(INVALID_REGULAR_RESERVATION_TIME);
    }

    @Test
    void 정기_예약_시간이_겹치면_정기_예약_생성_실패() throws JsonProcessingException {
        RegularReservationCreateRequest regularReservationCreateRequest = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                DayOfWeek.THURSDAY,
                "중복 테스트 팀",
                LocalTime.of(10,0),
                LocalTime.of(12,0),
                "C000001",
                LocalDate.now(),
                LocalDate.now().plusWeeks(1)
        );

        RegularReservationCreateRequest regularReservationCreateRequest2 = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                DayOfWeek.THURSDAY,
                "중복 테스트 팀",
                LocalTime.of(11,0),
                LocalTime.of(13,0),
                "C000001",
                LocalDate.now(),
                LocalDate.now().plusWeeks(1)
        );

        // 첫 정기 예약 생성은 성공
        given()
                .header(AUTHORIZATION, authorizationValue)
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(regularReservationCreateRequest))
        .when()
                .post("/admin/regular-reservation")
        .then()
                .statusCode(HttpStatus.SC_CREATED);

        // 겹친 정기 예약은 생성 실패
        String errorMessage =
                given()
                        .header(AUTHORIZATION, authorizationValue)
                        .header(CONTENT_TYPE, "application/json")
                        .body(mapper.writeValueAsString(regularReservationCreateRequest2))
                .when()
                        .post("/admin/regular-reservation")
                .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract().jsonPath().get("message");

        assertThat(errorMessage).isEqualTo(ANOTHER_REGULAR_RESERVATION_ALREADY_EXISTS);
    }

    @Test
    void 정기_예약_삭제_성공() throws JsonProcessingException {
        RegularReservationCreateRequest regularReservationCreateRequest = new RegularReservationCreateRequest(
                "TEAM",
                "ALL",
                DayOfWeek.SUNDAY,
                "삭제 테스트 팀",
                LocalTime.of(20,0),
                LocalTime.of(21,0),
                "C000001",
                LocalDate.now(),
                LocalDate.now().plusWeeks(1)
        );

        // 정기 예약 생성
        String location =
                given()
                        .header(AUTHORIZATION, authorizationValue)
                        .header(CONTENT_TYPE, "application/json")
                        .body(mapper.writeValueAsString(regularReservationCreateRequest))
                .when()
                        .post("/admin/regular-reservation")
                .then()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract().header("Location");

        Long id = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));

        // 정기 예약 삭제
        given()
                .header(AUTHORIZATION, authorizationValue)
                .header(CONTENT_TYPE, "application/json")
                .body(List.of(id))
        .when()
                .delete("/admin/regular-reservation")
        .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

}
