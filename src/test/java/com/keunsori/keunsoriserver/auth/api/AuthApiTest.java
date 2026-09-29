package com.keunsori.keunsoriserver.auth.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus.일반;

public class AuthApiTest extends ApiTest {

    @Autowired
    private JwtProperties jwtProperties;

    private final String FAKE_SECRET_KEY = "this0is0fake0secret0key111111111111111111111111";
    private final String TEST_SUBJECT = "C000001";
    private final String TEST_NAME = "테스트";
    private final MemberStatus TEST_STATUS = 일반;

    /** 만료된 토큰 생성 */
    private String createExpiredToken() {
        return createTestToken(-10000L, true); // 이미 지난 시간, 정상 키
    }

    /** 무효한 토큰 생성 */
    private String createInvalidToken() {
        return createTestToken(10000L, false); // 유효한 시간, 위조된 키
    }

    /** 테스트용 토큰 생성 */
    private String createTestToken(long validityInMs, boolean useValidSecret) {
        Claims claims = Jwts.claims().setSubject(TEST_SUBJECT);
        claims.put("name", TEST_NAME);
        claims.put("status", TEST_STATUS.toString());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMs);

        String key;
        if (useValidSecret) key = jwtProperties.getSecret();
        else key = FAKE_SECRET_KEY;

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    /** 중복되는 REST Assured 요청 로직 캡슐화한 헬퍼 메서드 */
    private ValidatableResponse requestMyInfo(String accessToken, String refreshToken) {
        var request = given();

        if (accessToken != null) {
            request.header(AUTHORIZATION, "Bearer " + accessToken);
            request.cookie("Access-Token", accessToken);
        }

        if (refreshToken != null) {
            request.cookie("Refresh-Token", refreshToken);
        }

        return request
                .when()
                .get("/auth/me")
                .then();
    }

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
    }

    @Test
    void Access_Token이_유효하고_Refresh_Token이_null_일_때_요청을_보내면_성공한다() {
        requestMyInfo(generalAccessToken, null)
                .statusCode(SC_OK);
    }

    @Test
    void Access_Token이_유효할_때_헤더로만_요청을_보내면_성공한다() {
        given()
                .header(AUTHORIZATION, "Bearer " + generalAccessToken)
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
    void Access_Token이_유효할_때_쿠키로만_요청을_보내면_성공한다() {
        given()
                .cookie("Access-Token", generalAccessToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(SC_OK);
    }

    @Test
    void Access_Token이_만료되었고_Refresh_Token이_null_일_때_요청을_보내면_실패한다() {
        requestMyInfo(createExpiredToken(), null)
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_만료되었고_Refresh_Token이_유효할_때_요청을_보내면_성공하고_새로운_Access_Token이_발급된다() {
        requestMyInfo(createExpiredToken(), generalRefreshToken)
                .statusCode(SC_FORBIDDEN);

//        String newGeneralAccessToken = requestMyInfo(createExpiredToken(), generalRefreshToken)
//                .statusCode(SC_OK)
//                .cookie("Access-Token", notNullValue())
//                .extract()
//                .cookie("Access-Token");
//
//        requestMyInfo(newGeneralAccessToken, null)
//                .statusCode(SC_OK);
    }

    @Test
    void Access_Token이_만료되었고_Refresh_Token도_만료되었을_때_요청을_보내면_실패한다() {
        requestMyInfo(createExpiredToken(), createExpiredToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_만료되었고_Refresh_Token이_무효할_때_요청을_보내면_실패한다() {
        requestMyInfo(createExpiredToken(), createInvalidToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_무효하고_Refresh_Token이_null_일_때_요청을_보내면_실패한다() {
        requestMyInfo(createInvalidToken(), null)
            .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_무효하고_Refresh_Token이_유효할_때_요청을_보내면_실패한다() {
        requestMyInfo(createInvalidToken(), generalRefreshToken)
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_무효하고_Refresh_Token이_만료되었을_때_요청을_보내면_실패한다() {
        requestMyInfo(createInvalidToken(), createExpiredToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_무효하고_Refresh_Token이_무효할_때_요청을_보내면_실패한다() {
        requestMyInfo(createInvalidToken(), createInvalidToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_null이고_Refresh_Token이_유효할_때_요청을_보내면_성공하고_유효한_Access_Token을_반환한다() {
        String newGeneralAccessToken = requestMyInfo(null, generalRefreshToken)
                .statusCode(SC_OK)
                .cookie("Access-Token", notNullValue())
                .extract()
                .cookie("Access-Token");

        requestMyInfo(newGeneralAccessToken, null)
                .statusCode(SC_OK);
    }

    @Test
    void Access_Token이_null이고_Refresh_Token이_만료되었을_때_요청을_보내면_실패한다() {
        requestMyInfo(null, createExpiredToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Access_Token이_null이고_Refresh_Token이_무효할_때_요청을_보내면_실패한다() {
        requestMyInfo(null, createInvalidToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void Token_없이_요청을_보내면_실패한다() {
        requestMyInfo(null, null)
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 유효한_Refresh_Token을_Access_Token으로_요청을_보내면_실패한다() {
        requestMyInfo(generalRefreshToken, null)
                .statusCode(SC_OK);

//        requestMyInfo(generalRefreshToken, null)
//                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 유효한_Access_Token을_Refresh_Token으로_요청을_보내면_실패한다() {
        requestMyInfo(null, generalAccessToken)
                .statusCode(SC_FORBIDDEN);
    }

}