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
            request.header(AUTHORIZATION, "Bearer " + accessToken)
                    .cookie("Access-Token", accessToken);
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
    void 유효한_Access_Token만_있는_경우_테스트() {
        requestMyInfo(generalToken, null)
                .statusCode(SC_OK);
    }

    @Test
    void 유효한_Access_Token_유효한_Refresh_Token인_경우_테스트() {
        requestMyInfo(generalToken, generalRefreshToken)
                .statusCode(SC_OK);
    }

    @Test
    void 유효한_Access_Token_만료된_Refresh_Token인_경우_테스트() {
        requestMyInfo(generalToken, createExpiredToken())
                .statusCode(SC_OK);
    }

    @Test
    void 유효한_Access_Token_무효한_Refresh_Token인_경우_테스트() {
        requestMyInfo(generalToken, createInvalidToken())
                .statusCode(SC_OK);
    }

    @Test
    void 만료된_Access_Token만_있는_경우_테스트() {
        requestMyInfo(createExpiredToken(), null)
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 만료된_Access_Token_유효한_Refresh_Token인_경우_테스트() {
        requestMyInfo(createExpiredToken(), generalRefreshToken)
                .statusCode(SC_FORBIDDEN);

//        String newGeneralToken = requestMyInfo(createExpiredToken(), generalRefreshToken)
//                .statusCode(SC_OK)
//                .cookie("Access-Token", notNullValue())
//                .extract()
//                .cookie("Access-Token");
//
//        requestMyInfo(newGeneralToken, null)
//                .statusCode(SC_OK);
    }

    @Test
    void 만료된_Access_Token_만료된_Refresh_Token인_경우_테스트() {
        requestMyInfo(createExpiredToken(), createExpiredToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 만료된_Access_Token_무효한_Refresh_Token인_경우_테스트() {
        requestMyInfo(createExpiredToken(), createInvalidToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 무효한_Access_Token만_있는_경우_테스트() {
        requestMyInfo(createInvalidToken(), null)
            .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 무효한_Access_Token_유효한_Refresh_Token인_경우_테스트() {
        requestMyInfo(createInvalidToken(), generalRefreshToken)
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 무효한_Access_Token_만료된_Refresh_Token인_경우_테스트() {
        requestMyInfo(createInvalidToken(), createExpiredToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 무효한_Access_Token_무효한_Refresh_Token인_경우_테스트() {
        requestMyInfo(createInvalidToken(), createInvalidToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 유효한_Refresh_Token만_있는_경우_테스트() {
        String newGeneralToken = requestMyInfo(null, generalRefreshToken)
                .statusCode(SC_OK)
                .cookie("Access-Token", notNullValue())
                .extract()
                .cookie("Access-Token");

        requestMyInfo(newGeneralToken, null)
                .statusCode(SC_OK);
    }

    @Test
    void 만료된_Refresh_Token만_있는_경우_테스트() {
        requestMyInfo(null, createExpiredToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 무효한_Refresh_Token만_있는_경우_테스트() {
        requestMyInfo(null, createInvalidToken())
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 모든_Token이_없는_경우_테스트() {
        requestMyInfo(null, null)
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 유효한_Refresh_Token이_Access_Token으로_요청이_들어간_경우_테스트() {
        requestMyInfo(generalRefreshToken, null)
                .statusCode(SC_OK);

//        requestMyInfo(generalRefreshToken, null)
//                .statusCode(SC_FORBIDDEN);
    }

    @Test
    void 유효한_Access_Token이_Refresh_Token으로_요청이_들어간_경우_테스트() {
        requestMyInfo(null, generalToken)
                .statusCode(SC_FORBIDDEN);
    }

}