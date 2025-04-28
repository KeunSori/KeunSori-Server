package com.keunsori.keunsoriserver.domain.auth.controller;

import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordFindRequest;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.service.AuthService;

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
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        authService.login(loginRequest.studentId(), response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "Refresh-Token", required = false) String refreshToken, HttpServletResponse response){
        authService.logout(refreshToken, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/find")
    public ResponseEntity<Void> findPassword(@Valid @RequestBody PasswordFindRequest request){
        authService.findPassword(request);
        return ResponseEntity.ok().build();
    }
}
