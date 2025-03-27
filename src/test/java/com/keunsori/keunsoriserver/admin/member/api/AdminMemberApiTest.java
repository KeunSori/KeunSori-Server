package com.keunsori.keunsoriserver.admin.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class AdminMemberApiTest extends ApiTest {
    private String authorizationValue;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_admin_member();
        authorizationValue = "Bearer " + adminToken;
    }

    @Test
    void 마이페이지_반환_성공(){
        given().
                header(AUTHORIZATION, authorizationValue).
                when().
                get("/admin/me").
                then().
                statusCode(HttpStatus.SC_OK);
    }


    @Test
    void 비밀번호_올바르게_입력하면_변경_성공() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("testadmin123!",
                "password123!");

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                patch("/admin/me/password").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 기존_비밀번호_잘못_입력하면_변경_실패() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("incorrect123!",
                "password123!");

        String errorMessage = given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                patch("/admin/me/password").
                then().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                extract().
                jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_CURRENT_PASSWORD);
    }
}
