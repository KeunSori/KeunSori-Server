package com.keunsori.keunsoriserver.domain.auth.controller;

import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordUpdateLinkSendRequest;
import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/password/update-link/send")
    @Operation(summary = "비밀번호를 변경할 수 있는 링크를 전송합니다.")
    public ResponseEntity<Void> sendPasswordUpdateLink(@Valid @RequestBody PasswordUpdateLinkSendRequest request) {
        authService.sendPasswordUpdateLink(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    @Operation(summary = "사용자가 클릭한 비밀번호 변경 링크 기준으로 비밀번호를 변경합니다.")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        authService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}
