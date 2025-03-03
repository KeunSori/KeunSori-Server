package com.keunsori.keunsoriserver.domain.auth.controller;



import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.service.AuthService;
import com.keunsori.keunsoriserver.domain.auth.service.TokenService;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_NOT_EXISTS;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Void> lgoin(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        Member member = memberRepository.findByStudentIdIgnoreCase(loginRequest.studentId())
                .orElseThrow(()->new MemberException(STUDENT_ID_NOT_EXISTS));

        authService.login(member, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(@CookieValue(value = "Refresh-Token", required = false) String refreshToken, HttpServletResponse response){
        authService.reissueToken(refreshToken, response);
        return ResponseEntity.ok("토큰 재발급 성공");
    }
    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "Refresh-Token", required = false) String refreshToken, HttpServletResponse response){
        authService.logout(refreshToken, response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    //Access Token 유효성 검사(보안 강화)
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String accessToken){
        boolean isValid= tokenService.validateToken(accessToken);

        if(isValid){
            return ResponseEntity.ok("Access Token 유효.");
        }
        else {
            return ResponseEntity.status(401).body("AccessToken 유효하지 않습니다.");
        }
    }
}
