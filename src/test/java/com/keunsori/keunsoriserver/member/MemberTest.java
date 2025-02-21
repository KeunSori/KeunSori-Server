package com.keunsori.keunsoriserver.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.common.DataCleaner;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.member.sign_up.dto.request.SignUpRequest;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MemberTest extends ApiTest {

    private String authorizationValue;

    @BeforeEach
    void login() throws JsonProcessingException {
        login_with_general_member();
        authorizationValue = "Bearer " + token;
    }

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private DataCleaner dataCleaner;

    @AfterEach
    public void clear() {
        dataCleaner.clear();
    }

    @Test
    void 회원가입_검증() {
        SignUpRequest request = new SignUpRequest("", "c111111","abc@gmail.com","asdfqwer123!", "asdfqwer123! ");
    }

    @Test
    public void 존재하지_않는_아이디_입력시_에러() throws JsonProcessingException {
        LoginRequest request = new LoginRequest("A123456", "wrongid123!");

        String errorMessage =
                given().
                    header(CONTENT_TYPE, "application/json").
                    body(mapper.writeValueAsString(request)).
                when().
                    post("/auth/login").
                then().
                    statusCode(HttpStatus.SC_BAD_REQUEST).
                    extract().
                    jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo("존재하지 않는 학번입니다.");
    }

    @Test
    public void 잘못된_비밀번호_에러() throws JsonProcessingException {
        LoginRequest request = new LoginRequest("C011013", "goodbye123!");

        String errorMessage =
                given().
                    header(CONTENT_TYPE, "application/json").
                    body(mapper.writeValueAsString(request)).
                when().
                    post("/auth/login").
                then().
                    statusCode(HttpStatus.SC_UNAUTHORIZED).
                    extract().
                    jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo("비밀번호가 일치하지 않습니다.");
    }

    //reissue관련

    //validateToken

//학번 중복체크
    @Test
    void 학번_중복되면_가입_실패() throws JsonProcessingException {
        SignUpRequest request0 = new SignUpRequest("테스트", "Q000001", "tester0@gmail.com", "xptmxm123!", "xptmxm123!");
        SignUpRequest request1 = new SignUpRequest("테스트", "Q000001", "tester1@gmail.com", "xptmxm123!", "xptmxm123!");
        SignUpRequest request2 = new SignUpRequest("테스트", "q000001", "tester2@gmail.com", "xptmxm123!", "xptmxm123!");
                given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request0)).
                        when().
                        post("/signup").
                        then().
                        statusCode(HttpStatus.SC_OK);

        String errorMessage1 =
                given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request1)).
                        when().
                        post("/signup").
                        then().
                        statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract().
                        jsonPath().get("message");

        String errorMessage2 =
                given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request2)).
                        when().
                        post("/signup").
                        then().
                        statusCode(HttpStatus.SC_OK)
                        .extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage1).isEqualTo(DUPLICATED_STUDENT_ID);
        //Assertions.assertThat(errorMessage2).isEqualTo(DUPLICATED_STUDENT_ID);
    }

    //이메일 중복체크
    @Test
    void 이메일_중복되면_가입_실패() throws JsonProcessingException {
        SignUpRequest request1 = new SignUpRequest("테스트", "A000000", "test@gmail.com", "xptmxm123!", "xptmxm123!");
        SignUpRequest request2 = new SignUpRequest("테스트", "B000000", "test@gmail.com", "xptmxm123!", "xptmxm123!");

                given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request1)).
                        when().
                        post("/signup").
                        then().
                        statusCode(HttpStatus.SC_OK);

        String errorMessage =
                given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request2)).
                        when().
                        post("/signup").
                        then().
                        statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract().
                        jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(DUPLICATED_EMAIL);
    }


    //비밀번호, 비밀번호 확인 일치하는지 확인
    @Test
    void 비밀번호와_비밀번호_확인_필드_불일치하면_가입_실패() throws JsonProcessingException {
        SignUpRequest request = new SignUpRequest("테스트", "A111111", "test@gmail.com", "xptmxm123!", "xptmxm123@");
        String errorMessage =
                given().
                    header(CONTENT_TYPE, "application/json").
                    body(mapper.writeValueAsString(request)).
                when().
                    post("/signup").
                then().
                    statusCode(HttpStatus.SC_BAD_REQUEST)
                    .extract().
                    jsonPath().get("message");

        Assertions.assertThat(errorMessage).isEqualTo(PASSWORD_IS_DIFFERENT_FROM_CHECK);
    }

    @Test
    void 대소문자_상관_없이_회원가입하면_대문자로_저장() throws JsonProcessingException {
        SignUpRequest request1 = new SignUpRequest("테스트", "A000001", "test1@gmail.com", "xptmxm123!", "xptmxm123!");
        SignUpRequest request2 = new SignUpRequest("테스트", "a000002", "test2@gmail.com", "xptmxm123!", "xptmxm123!");

        String savedId1 =
                given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request1)).
                when().
                        post("/signup").
                then().
                        statusCode(HttpStatus.SC_OK)
                        .extract().
                        jsonPath().get("studentId");

        String savedId2 =
                given().
                        header(CONTENT_TYPE, "application/json").
                        body(mapper.writeValueAsString(request2)).
                when().
                        post("/signup").
                then().
                        statusCode(HttpStatus.SC_OK)
                        .extract().
                        jsonPath().get("studentId");

        Assertions.assertThat(savedId1).isEqualTo("A000001");
        Assertions.assertThat(savedId2).isEqualTo("A000002");
    }

    @Test
    void 대소문자_구분_없이_로그인_성공() throws JsonProcessingException {
        LoginRequest request1 = new LoginRequest("C011013", "hello123!");
        LoginRequest request2 = new LoginRequest("c011013", "hello123!");

        given().
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request1)).
        when().
                post("/auth/login").
        then().
                statusCode(HttpStatus.SC_OK);

        given().
                header(CONTENT_TYPE, "application/json").
                body(mapper.writeValueAsString(request2)).
        when().
                post("/auth/login").
        then().
                statusCode(HttpStatus.SC_OK);
    }
}
