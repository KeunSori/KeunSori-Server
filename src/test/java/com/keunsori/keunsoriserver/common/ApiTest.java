package com.keunsori.keunsoriserver.common;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.keunsori.keunsoriserver.admin.member.fixture.MemberFixture;
import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApiTest {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private DataCleaner dataCleaner;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected String generalToken;
    protected String generalRefreshToken;

    protected String adminToken;

    @AfterEach
    public void clear() {
        dataCleaner.clear();
    }

    @Test
    public void login_with_general_member() throws JsonProcessingException {
        memberRepository.deleteAll();
        Member member = MemberFixture.GENERAL1();
        memberRepository.save(member);

        LoginRequest request = new LoginRequest("C000001", "test123!");

        var response = given()
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request)).
                when().
                post("/auth/login").
                then().
                        statusCode(SC_OK).
                        extract().
                        response();
        generalToken = response.getCookie("Access-Token");
        generalRefreshToken = response.getCookie("Refresh-Token");
    }

    @Test
    public void login_with_admin_member() throws JsonProcessingException {
        memberRepository.deleteAll();
        Member member = MemberFixture.ADMIN();
        memberRepository.save(member);

        LoginRequest request = new LoginRequest("A000001", "testadmin123!");

        adminToken = given()
                .header(CONTENT_TYPE, "application/json")
                .body(mapper.writeValueAsString(request)).
                when().
                post("/auth/login").
                then().
                statusCode(SC_OK).
                extract().
                cookie("Access-Token");
    }
}
