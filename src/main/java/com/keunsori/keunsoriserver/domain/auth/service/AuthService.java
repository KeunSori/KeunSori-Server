package com.keunsori.keunsoriserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;

    //Refresh Token Redis에 저장
    public void saveRefreshToken(String studentId, String refreshToken, long expirationTime) {
        redisTemplate.opsForValue().set(studentId, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    //Refresh Token 조회
    public String getRefreshToken(String studentId) {
        return redisTemplate.opsForValue().get(studentId);
    }

    //Refresh Token 삭제
    public void deleteRefreshToken(String studentId) {
        redisTemplate.delete(studentId);

    }
}
