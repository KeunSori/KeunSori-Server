package com.keunsori.keunsoriserver.reservation.api;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.reservation.dto.response.ReservationResponse;

import java.util.List;


public class ReservationApiTest extends ApiTest {

    @Test
    void 내_예약_다건_조회_성공() throws JsonProcessingException {
        login_with_general_member();

        List<ReservationResponse> response =
        given().
                header(AUTHORIZATION, "Bearer " + token).
        when().
                get("/reservation/my").
        then().
                statusCode(200).
                extract().
                jsonPath().getList(".", ReservationResponse.class);

        Assertions.assertThat(response).hasSize(2);
    }
}
