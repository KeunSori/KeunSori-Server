package com.keunsori.keunsoriserver.domain.auth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    //Refresh Token 저장하기
    public void saveRefreshToken(String studentId, String refreshToken, long expirationTime) {
        redisTemplate.opsForValue().set(studentId,refreshToken,expirationTime, TimeUnit.MILLISECONDS);
    }

    //Refersh Token 조회하기
    public String getRefreshToken(String studentId) {
        return redisTemplate.opsForValue().get(studentId);
    }

    //Refresh Token 삭제하기
    public void deleteRefreshToken(String studentId) {
        redisTemplate.delete(studentId);
    }
}
