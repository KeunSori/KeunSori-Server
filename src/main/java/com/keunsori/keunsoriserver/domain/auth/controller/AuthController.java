package com.keunsori.keunsoriserver.domain.auth.controller;

import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordUpdateLinkSendRequest;
import com.keunsori.keunsoriserver.domain.auth.dto.request.PasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.auth.dto.response.AuthCheckResponse;
import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginResponse;
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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        LoginResponse loginResponse = authService.login(loginRequest, response);
        return ResponseEntity.ok(loginResponse);
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

    @GetMapping("/me")
    @Operation(summary = "클라이언트가 로그인 상태일 경우 권한을 반환합니다.")
    public ResponseEntity<AuthCheckResponse> checkAuth(){
        AuthCheckResponse response = authService.checkAuth();
        return ResponseEntity.ok(response);
    }
}
