package com.keunsori.keunsoriserver.common;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApiTest {

    protected ObjectMapper mapper = new ObjectMapper();

    protected String token;

    @Test
    public void login_with_general_member() throws JsonProcessingException {
        LoginRequest request = new LoginRequest("C011013", "hello123!");

        token = given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request)).
                when().
                        post("/auth/login").
                then().
                        extract().
                        jsonPath().getString("accessToken");
    }
}
