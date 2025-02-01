package com.keunsori.keunsoriserver.common;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApiTest {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private DataCleaner dataCleaner;

    protected String token;

    @AfterEach
    public void clear() {
        dataCleaner.clear();
    }

    @Test
    public void login_with_general_member() throws JsonProcessingException {
        LoginRequest request = new LoginRequest("C011013", "hello123!");

        token = given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request)).
                when().
                        post("/auth/login").
                then().
                        statusCode(SC_OK).
                        extract().
                        jsonPath().getString("accessToken");
    }
}
