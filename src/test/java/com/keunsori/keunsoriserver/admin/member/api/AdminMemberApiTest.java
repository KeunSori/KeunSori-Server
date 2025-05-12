package com.keunsori.keunsoriserver.admin.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class AdminMemberApiTest extends ApiTest {

    @Autowired
    private MemberRepository memberRepository;

    private String authorizationValue;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_admin_member();
        authorizationValue = "Bearer " + adminToken;
    }

    @Test
    void 마이페이지_반환_성공(){
        given().
                header(AUTHORIZATION, authorizationValue).
                when().
                get("/admin/me").
                then().
                statusCode(HttpStatus.SC_OK);
    }


    @Test
    void 비밀번호_올바르게_입력하면_변경_성공() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("testadmin123!",
                "password123!");

        given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                patch("/admin/me/password").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 기존_비밀번호_잘못_입력하면_변경_실패() throws JsonProcessingException {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("incorrect123!",
                "password123!");

        String errorMessage = given().
                header(AUTHORIZATION, authorizationValue).
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request)).
                when().
                patch("/admin/me/password").
                then().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                extract().
                jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_CURRENT_PASSWORD);
    }

    @Test
    void 회원_리스트_조회에_성공한다(){
        given().
                header(AUTHORIZATION, authorizationValue).
                when().
                get("/admin/members/list").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 선택한_회원_탈퇴처리에_성공한다(){
        Member member = Member.builder()
                .studentId("Q012345")
                .email("email@google.com")
                .password("password123!")
                .name("테스트")
                .status(MemberStatus.일반)
                .build();
        memberRepository.save(member);

        given().
                header(AUTHORIZATION, authorizationValue).
                when().
                delete("/admin/members/" + member.getId()).
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void 존재하지_않는_아이디로_탈퇴처리하면_예외가_발생한다(){

        String errorMessage =
                given().
                        header(AUTHORIZATION, authorizationValue).
                        when().
                        delete("/admin/members/0").
                        then().
                        statusCode(HttpStatus.SC_BAD_REQUEST).
                        extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(MEMBER_NOT_EXISTS_WITH_STUDENT_ID);
    }

    @Test
    void 가입_신청자_리스트_반환에_성공한다(){
        given().
                header(AUTHORIZATION, authorizationValue).
                when().
                get("/admin/members/applicants").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 선택한_신청자_가입_승인_처리에_성공한다(){
        Member member = Member.builder()
                .studentId("Q012345")
                .email("email@google.com")
                .password("password123!")
                .name("테스트")
                .status(MemberStatus.승인대기)
                .build();
        memberRepository.save(member);

        given().
                header(AUTHORIZATION, authorizationValue).
                when().
                patch("/admin/members/" + member.getId() + "/approve").
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void 승인대기_상태가_아닌_회원_가입_승인하면_예외_발생(){
        Member member = Member.builder()
                .studentId("Q012345")
                .email("email@google.com")
                .password("password123!")
                .name("테스트")
                .status(MemberStatus.일반)
                .build();
        memberRepository.save(member);

        String errorMessage =
                given().
                        header(AUTHORIZATION, authorizationValue).
                        when().
                        patch("/admin/members/" + member.getId() + "/approve").
                        then().
                        statusCode(HttpStatus.SC_BAD_REQUEST).
                        extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(INVALID_STATUS_FOR_APPROVAL);
    }
}
