package com.keunsori.keunsoriserver.member.api;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.DUPLICATED_STUDENT_ID;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.assertj.core.api.Assertions;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.dto.request.SignUpRequest;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.dto.response.ReservationResponse;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MemberApiTest extends ApiTest {

    private String memberAuth;
    private String adminAuth;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void login() throws JsonProcessingException {
        loginSetting();
        memberAuth = "Bearer " + memberToken;
        adminAuth = "Bearer " + adminToken;
    }

    @Test
    void 회원가입시_학번이_중복되면_실패한다() throws JsonProcessingException {
        SignUpRequest request = new SignUpRequest(
                "회원",
                "C011003",
                "test3@example.com",
                "test123!",
                "test123!"
        );

        given()
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/signup")
        .then()
                .statusCode(200);

        String message =
        given()
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/signup")
        .then()
                .statusCode(400)
                .extract().jsonPath().getString("message");

        Assertions.assertThat(message).isEqualTo(DUPLICATED_STUDENT_ID);
    }

    @Test
    void 회원가입시_대소문자가_다른_학번이_중복되면_실패한다() throws JsonProcessingException {
        SignUpRequest request = new SignUpRequest(
                "회원",
                "C011003",
                "test3@example.com",
                "test123!",
                "test123!"
        );

        SignUpRequest request2 = new SignUpRequest(
                "회원",
                "c011003",
                "test4@example.com",
                "test123!",
                "test123!"
        );

        given()
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .post("/signup")
                .then()
                .statusCode(200);

        String message =
                given()
                        .header(CONTENT_TYPE, "application/json")
                        .body(mapper.writeValueAsString(request2))
                        .when()
                        .post("/signup")
                        .then()
                        .statusCode(400)
                        .extract().jsonPath().getString("message");

        Assertions.assertThat(message).isEqualTo(DUPLICATED_STUDENT_ID);
    }

    @Test
    void 회원탈퇴시_탈퇴시점_이후의_예약은_hard_delete된다() throws JsonProcessingException {
        // 테스트 회원의 예약 생성
        ReservationCreateRequest reservationCreateRequest = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2999, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        given().
                header(AUTHORIZATION, memberAuth).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(reservationCreateRequest)).
        when().
                post("/reservation").
        then().
                statusCode(HttpStatus.SC_CREATED);

        // 테스트 회원 탈퇴
        given().
                header(AUTHORIZATION, adminAuth).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(reservationCreateRequest)).
        when().
                delete("/admin/members/" + memberId).
        then().
                statusCode(HttpStatus.SC_NO_CONTENT);

        // 멤버 예약 조회
        List<Reservation> reservationList = reservationRepository.findAll();

        Assertions.assertThat(reservationList).isEmpty();
    }

    @Test
    void 회원탈퇴시_탈퇴시점_이전의_예약은_soft_delete된다() throws JsonProcessingException {
        // 테스트 회원의 예약 생성
        Member member = memberRepository.findByStudentId("C011001")
                .orElseThrow(() -> new RuntimeException("테스트 멤버가 없습니다."));

        ReservationCreateRequest reservationCreateRequest = new ReservationCreateRequest(
                "PERSONAL",
                "DRUM",
                LocalDate.of(2000, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)
        );

        Reservation reservation = Reservation.builder()
                .date(LocalDate.of(2000, 1, 1))
                .reservationType(ReservationType.PERSONAL)
                .session(Session.DRUM)
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .member(member)
                .build();

        reservationRepository.save(reservation);

        // 테스트 회원 탈퇴
        given().
                header(AUTHORIZATION, adminAuth).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(reservationCreateRequest)).
        when().
                delete("/admin/members/" + memberId).
        then().
                statusCode(HttpStatus.SC_NO_CONTENT);

        // 예약 현황 조회
        List<ReservationResponse> reservationList =
                given().
                        header(AUTHORIZATION, adminAuth).
                when().
                        get("/reservation/list?month=200001").
                then().
                        statusCode(HttpStatus.SC_OK).
                        extract().
                        jsonPath().getList(".", ReservationResponse.class);

        Assertions.assertThat(reservationList).isNotEmpty();
        ReservationResponse reservationResponse = reservationList.get(0);
        Assertions.assertThat(reservationResponse.reservationMemberName()).isEqualTo("(알 수 없음)");
    }
}
