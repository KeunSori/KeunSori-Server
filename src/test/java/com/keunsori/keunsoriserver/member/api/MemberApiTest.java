package com.keunsori.keunsoriserver.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.PASSWORD_IS_DIFFERENT_FROM_CHECK;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.PASSWORD_NOT_CORRECT;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class MemberApiTest extends ApiTest {

    private String authorizationValue;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
        authorizationValue = "Bearer " + token;
    }

    @Test
    void 마이페이지_반환_성공(){
        given().
                header(AUTHORIZATION, authorizationValue).
                when().
                get("/members/me").
                then().
                statusCode(HttpStatus.SC_OK);
    }


    @Test
    void 비밀번호_올바르게_입력하면_변경_성공() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("test123!",
                "password123!", "password123!");

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                patch("/members/me/password").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 기존_비밀번호_잘못_입력하면_변경_실패() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("incorrect123!",
                "password123!", "password123!");


        String errorMessage = given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                patch("/members/me/password").
                then().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                extract().
                jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(PASSWORD_NOT_CORRECT);
    }

    @Test
    void 비밀번호_확인_잘못_입력하면_변경_실패() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("test123!",
                "password123!", "password123#");


        String errorMessage = given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                patch("/members/me/password").
                then().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                extract().
                jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(PASSWORD_IS_DIFFERENT_FROM_CHECK);
    }

}
