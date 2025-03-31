package com.keunsori.keunsoriserver.global.util;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.EXPIRED_TOKEN;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_TOKEN;

@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;

    // Access Token 생성
    public String generateAccessToken(String studentId, String name, MemberStatus status) {
        return createToken(studentId, name, status, jwtProperties.ACCESS_TOKEN_VALIDITY_TIME);
    }

    // Refresh Token 생성 (Redis에 저장 포함)
    public String generateRefreshToken(String studentId, String name, MemberStatus status) {
        String refreshToken = createToken(studentId, name, status, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(studentId, refreshToken, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME, TimeUnit.MILLISECONDS);

        return createToken(studentId, name, status, jwtProperties.REFRESH_TOKEN_VALIDITY_TIME);
    }

    // JWT 토큰 생성 로직
    private String createToken(String studentId, String name, MemberStatus status, long validity) {
        Claims claims = Jwts.claims().setSubject(studentId);
        claims.put("name", name);
        claims.put("status", status.toString());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    // 토큰 유효성 검사
    public void validateToken(String token) {
        try {
            token = removePrefix(token);  // Bearer 접두어 제거 후 검증
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
           throw new AuthException(EXPIRED_TOKEN); // 토큰 만료
        } catch (Exception e) {
            throw new AuthException(INVALID_TOKEN); // 유효하지 않은 토큰
        }
    }

    // 학번(StudentId) 조회
    public String getStudentIdFromToken(String token) {
        token = removePrefix(token);  // Bearer 접두어 제거
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 상태(Status) 조회
    public String getStatusFromToken(String token) {
        token = removePrefix(token);  // Bearer 접두어 제거
        return (String) Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .get("status");
    }

    // 토큰 만료 시간 조회
    public Long getExpirationTime(String token) {
        token = removePrefix(token);  // Bearer 접두어 제거
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    // Swagger에서 테스트할 경우 Bearer 접두어 제거
    private String removePrefix(String token) {
        String prefix = jwtProperties.PREFIX;
        if (token != null && token.startsWith(prefix + " ")) {
            return token.substring(prefix.length() + 1);
        }
        return token;
    }
}
