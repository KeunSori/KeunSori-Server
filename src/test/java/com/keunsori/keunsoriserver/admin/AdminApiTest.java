package com.keunsori.keunsoriserver.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keunsori.keunsoriserver.common.DataCleaner;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AdminApiTest {

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
    public void login_with_admin_member() throws JsonProcessingException {
        memberRepository.deleteAll();
        Member member = Member.builder()
                .studentId("A000001")
                .email("testAdmin@g.hongik.ac.kr")
                .password(passwordEncoder.encode("testadmin123!"))
                .status(MemberStatus.관리자)
                .build();
        memberRepository.save(member);

        LoginRequest request = new LoginRequest("A000001", "testadmin123!");

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
