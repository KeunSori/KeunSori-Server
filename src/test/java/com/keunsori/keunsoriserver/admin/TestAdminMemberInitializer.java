package com.keunsori.keunsoriserver.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
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
public class TestAdminMemberInitializer {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    protected ObjectMapper mapper;

    protected String token;

    @Test
    public void initializeTestAdminMember() throws JsonProcessingException {
        String studentId = "A000001";

        memberRepository.findByStudentId(studentId)
                .ifPresent(memberRepository::delete);

        Member member = Member.builder()
                .studentId("A000001")
                .email("testAdmin@g.hongik.ac.kr")
                .password(passwordEncoder.encode("passworda123!"))
                .name("관리자")
                .status(MemberStatus.관리자)
                .build();
        memberRepository.save(member);

        LoginRequest request = new LoginRequest("A000001", "passworda123!");

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
