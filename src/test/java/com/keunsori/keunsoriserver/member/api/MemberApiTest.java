package com.keunsori.keunsoriserver.member.api;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.DUPLICATED_STUDENT_ID;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.member.dto.request.SignUpRequest;

public class MemberApiTest extends ApiTest {

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
                .extract().jsonPath().getString("message");

        Assertions.assertThat(message).isEqualTo(DUPLICATED_STUDENT_ID);
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

        Assertions.assertThat(message).isEqualTo(DUPLICATED_STUDENT_ID);
    }

}
