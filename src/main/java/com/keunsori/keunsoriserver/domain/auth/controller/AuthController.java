package com.keunsori.keunsoriserver.domain.auth.controller;



import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.service.AuthService;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> lgoin(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        authService.login(loginRequest.studentId(), response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(@CookieValue(value = "Refresh-Token", required = false) String refreshToken, HttpServletResponse response){
        authService.reissueToken(refreshToken, response);
        return ResponseEntity.ok().build();
    }
    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "Refresh-Token", required = false) String refreshToken, HttpServletResponse response){
        authService.logout(refreshToken, response);
        return ResponseEntity.ok("로그아웃 성공");
    }
}
