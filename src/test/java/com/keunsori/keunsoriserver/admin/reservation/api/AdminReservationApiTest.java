package com.keunsori.keunsoriserver.admin.reservation.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleUpdateRequest;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.springframework.http.HttpHeaders.*;

public class AdminReservationApiTest extends ApiTest {
    private String authorizationValue;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_admin_member();
        authorizationValue = "Bearer " + adminToken;
    }

    @Test
    void 주간_예약_관리_페이지_반환_성공() {
        given().
                header(AUTHORIZATION, authorizationValue).
        when().
                get("/admin/reservation/weekly-schedule").
        then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 주간_설정_성공() throws JsonProcessingException {
        List<WeeklyScheduleUpdateRequest> requests = new ArrayList<>();

        requests.add(new WeeklyScheduleUpdateRequest(0, false, LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        requests.add(new WeeklyScheduleUpdateRequest(1, true, LocalTime.parse("10:00"), LocalTime.parse("22:00")));
        requests.add(new WeeklyScheduleUpdateRequest(2, false, LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        requests.add(new WeeklyScheduleUpdateRequest(3, false, LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        requests.add(new WeeklyScheduleUpdateRequest(4, false, LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        requests.add(new WeeklyScheduleUpdateRequest(5, true, LocalTime.parse("10:00"), LocalTime.parse("22:00")));
        requests.add(new WeeklyScheduleUpdateRequest(6, false, LocalTime.parse("10:00"), LocalTime.parse("11:00")));

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(requests)).
        when().
                put("/admin/reservation/weekly-schedule").
        then().
                statusCode(HttpStatus.SC_OK);
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
    void 관리자가_예약_삭제에_성공한다() throws JsonProcessingException {
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
                delete("admin/reservation/" + reservation.getId()).
        then().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }
}
