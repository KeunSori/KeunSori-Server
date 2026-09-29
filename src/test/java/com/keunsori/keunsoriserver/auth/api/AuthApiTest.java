package com.keunsori.keunsoriserver.auth.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.global.util.TokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AuthApiTest extends ApiTest {
    @Value("${jwt.secret}")
    private String secretKey;
    private String authorizationValue;
    @Autowired
    private TokenUtil tokenUtil;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
        authorizationValue = "Bearer " + generalToken;
    }

    @Test
    @DisplayName("Header에 유효한 토큰 존재")
    void 로그인_되어있는_경우_테스트() throws JsonProcessingException {
        given()
                .header(AUTHORIZATION, authorizationValue)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Cookie에 유효한 토큰 존재")
    void 로그인_되어있는_경우_쿠키_테스트(){
        // 헤더 없이 쿠키만으로 인증이 되지 않는가?
        given()
                .cookie("Access-Token", generalToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_OK)
                .body("role", equalTo("일반"));
    }

    @Test
    void 관리자_계정_로그인_되어있는_경우_테스트() throws JsonProcessingException {
        login_with_admin_member();
        String adminAuthorizationValue = "Bearer " + adminToken;

        given()
                .header(AUTHORIZATION, adminAuthorizationValue)
                .cookie("Access-Token", adminToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_OK)
                .body("role", equalTo("관리자"));
    }

    @Test
    @DisplayName("토큰 부존재")
    void 로그인_되어있지_않은_경우_테스트() {
        given()
                .when()
                .get("/auth/me") // Authorization / Cookie 없이 호출
                .then()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("토큰 값 위조 혹은 형식 외 토큰")
    void 유효하지_않은_토큰_테스트(){
        // Bearer가 앞에 붙지 않은 잘못된 형식
        given()
                .header(AUTHORIZATION, "InvalidToken " + generalToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_UNAUTHORIZED);

        // 위조된 토큰
        String tamperedToken = generalToken.substring(0, generalToken.length() - 1) + "a";
        given()
                .header(AUTHORIZATION, "Bearer " + tamperedToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("만료된 토큰")
    void 만료된_토큰_테스트(){
        String studentId = tokenUtil.getStudentIdFromToken(generalToken);

        String expiredToken = createExpiredToken(studentId);

        given()
                .header(AUTHORIZATION, "Bearer " + expiredToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_UNAUTHORIZED);
    }

    private String createExpiredToken(String studentId){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date past = new Date(now.getTime() - 60000);

        return Jwts.builder()
                .setSubject(studentId)
                .setIssuedAt(now)
                .setExpiration(past)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
