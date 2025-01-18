package com.keunsori.keunsoriserver.domain.auth.controller;

import com.keunsori.keunsoriserver.domain.auth.redis.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.auth.login.JwtTokenManager;
import com.keunsori.keunsoriserver.domain.auth.login.dto.response.LoginResponse;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenManager jwtTokenManager;
    private final RefreshTokenService authService;

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@RequestHeader("Refresh-Token") String refreshToken){
        //Refresh Token에서 studentId 뽑아내기
        String studentId= jwtTokenManager.getStudentIdFromToken(refreshToken);

        //Redis에 저장된 Refresh Token과 일치하는지 확인
        String storedRefreshToken=authService.getRefreshToken(studentId);
        if(storedRefreshToken==null || !storedRefreshToken.equals(refreshToken)){
            throw new AuthException("유효하지 않은 Refresh Token");
        }

        //새로운 AccessToken 생성
        String newAccessToken= jwtTokenManager.generateAccessToken(studentId, "Name", MemberStatus.일반);

        //새로운 Acess Token 반환
        return ResponseEntity.ok(new LoginResponse(
                newAccessToken,
                refreshToken,
                String.valueOf(jwtTokenManager.getExpirationTime(newAccessToken))));
    }
    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken){
        String studentId= jwtTokenManager.getStudentIdFromToken(accessToken);

        authService.deleteRefreshToken(studentId);

        return ResponseEntity.ok("로그아웃 됐습니다.");
    }

    //Access Token 유효성 검사(보안 강화)
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String accessToken){
        boolean isValid= jwtTokenManager.validateToken(accessToken);

        if(isValid){
            return ResponseEntity.ok("Access Token 유효.");
        }
        else {
            return ResponseEntity.status(401).body("AccessToken 유효하지 않습니다.");
        }

    }
}
