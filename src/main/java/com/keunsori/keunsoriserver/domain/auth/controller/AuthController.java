package com.keunsori.keunsoriserver.domain.auth.controller;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_REFRESH_TOKEN;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.MEMBER_NOT_EXISTS_WITH_STUDENT_ID;

import com.keunsori.keunsoriserver.domain.auth.login.LoginService;
import com.keunsori.keunsoriserver.domain.auth.login.dto.request.LoginRequest;
import com.keunsori.keunsoriserver.domain.auth.redis.RefreshTokenService;
import com.keunsori.keunsoriserver.domain.auth.login.JwtTokenManager;
import com.keunsori.keunsoriserver.domain.auth.login.dto.response.LoginResponse;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.exception.MemberException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final JwtTokenManager jwtTokenManager;
    private final RefreshTokenService authService;
    private final MemberRepository memberRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)  {
        LoginResponse loginResponse = loginService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@RequestHeader("Refresh-Token") String refreshToken){
        //Refresh Token에서 studentId 뽑아내기
        String studentId = jwtTokenManager.getStudentIdFromToken(refreshToken);

        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        //Redis에 저장된 Refresh Token과 일치하는지 확인
        String storedRefreshToken = authService.getRefreshToken(studentId);
        if(storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }

        //새로운 AccessToken 생성
        String newAccessToken = jwtTokenManager.generateAccessToken(studentId, member.getName(), member.getStatus());

        //새로운 Acess Token 반환
        return ResponseEntity.ok(new LoginResponse(
                newAccessToken,
                refreshToken,
                String.valueOf(jwtTokenManager.getExpirationTime(newAccessToken)),
                member.getName(),
                member.getStatus()
        ));
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
