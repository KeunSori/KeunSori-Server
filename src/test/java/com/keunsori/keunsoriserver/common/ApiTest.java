package com.keunsori.keunsoriserver.common;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

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
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.request.SignUpRequest;
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

    protected String token;

    @AfterEach
    public void clear() {
        dataCleaner.clear();
    }

    @Test
    public void login_with_general_member() throws JsonProcessingException {
        memberRepository.deleteAll();
        Member member = Member.builder()
                .studentId("C000001")
                .email("testMember@example.com")
                .password(passwordEncoder.encode("test123!"))
                .status(MemberStatus.일반)
                .build();
        memberRepository.save(member);

        LoginRequest request = new LoginRequest("C000001", "test123!");

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
