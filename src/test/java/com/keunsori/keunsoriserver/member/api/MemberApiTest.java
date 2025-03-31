package com.keunsori.keunsoriserver.member.api;

import static com.keunsori.keunsoriserver.global.exception.ErrorCode.*;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.member.dto.request.SignUpRequest;

public class MemberApiTest extends ApiTest {
    private String authorizationValue;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
        authorizationValue = "Bearer " + generalToken;
    }

    @Test
    void 회원가입시_학번이_중복되면_실패한다() throws JsonProcessingException {
        SignUpRequest request = new SignUpRequest(
                "회원",
                "C011001",
                "test@example.com",
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
                .extract().jsonPath().
                getString("message");

        Assertions.assertThat(message).isEqualTo(DUPLICATED_STUDENT_ID.getMassage());
    }

    @Test
    void 회원가입시_대소문자가_다른_학번이_중복되면_실패한다() throws JsonProcessingException {
        SignUpRequest request = new SignUpRequest(
                "회원",
                "C011001",
                "test@example.com",
                "test123!",
                "test123!"
        );

        SignUpRequest request2 = new SignUpRequest(
                "회원",
                "c011001",
                "test2@example.com",
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

        Assertions.assertThat(message).isEqualTo(DUPLICATED_STUDENT_ID.getMassage());
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
                "password123!");

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
                "password123!");


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

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_CURRENT_PASSWORD.getMassage());
    }

    @Test
    void 기존_비밀번호와_새_비밀번호가_같으면_에러_반환() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("test123!",
                "test123!");

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

        Assertions.assertThat(errorMessage).isEqualTo(PASSWORD_SAME_AS_OLD.getMassage());
    }



    @Test
    void 새_비밀번호_패턴_안맞으면_에러_반환() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("test123!",
                "password");

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

        Assertions.assertThat(errorMessage).isEqualTo(PASSWORD_INVALID_FORMAT.getMassage());
    }
}
