package com.keunsori.keunsoriserver.common;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;

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

    protected String memberToken;

    protected String adminToken;

    protected Long memberId;

    protected Long adminId;

    @AfterEach
    public void clear() {
        dataCleaner.clear();
    }

    @Test
    public void loginSetting() throws JsonProcessingException {
        memberRepository.deleteAll();
        Member member = Member.builder()
                .studentId("C011001")
                .email("test@example.com")
                .password(passwordEncoder.encode("test123!"))
                .status(MemberStatus.일반)
                .build();
        memberRepository.save(member);
        memberId = member.getId();

        Member adminMember = Member.builder()
                .studentId("C011002")
                .email("test2@example.com")
                .password(passwordEncoder.encode("test123!"))
                .status(MemberStatus.관리자)
                .build();
        memberRepository.save(adminMember);
        adminId = adminMember.getId();

        LoginRequest memberLoginRequest = new LoginRequest("C011001", "test123!");
        LoginRequest adminLoginRequest = new LoginRequest("C011002", "test123!");

        memberToken = given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(memberLoginRequest)).
                when().
                        post("/auth/login").
                then().
                        statusCode(SC_OK).
                        extract().
                        jsonPath().getString("accessToken");

        adminToken = given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(adminLoginRequest)).
                when().
                        post("/auth/login").
                then().
                        statusCode(SC_OK).
                        extract().
                        jsonPath().getString("accessToken");
    }
}
