package com.keunsori.keunsoriserver.auth.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AuthApiTest extends ApiTest {
    private String authorizationValue;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
        authorizationValue = "Bearer " + generalToken;
    }

    @Test
    void 로그인_되어있는_경우_테스트() throws JsonProcessingException {
        given()
                .header(AUTHORIZATION, authorizationValue)
                .cookie("Access-Token", generalToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_OK);
    }

    @Test
    void 로그인_되어있지_않은_경우_테스트() {
        given()
                .when()
                .get("/auth/me") // Authorization / Cookie 없이 호출
                .then()
                .statusCode(SC_FORBIDDEN);
    }
}
