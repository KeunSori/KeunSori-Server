package com.keunsori.keunsoriserver.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;


import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final StringRedisTemplate stringRedisTemplate;

    //Refresh Token 저장하기
    public void saveRefreshToken(String studentId, String refreshToken, long expirationTime) {
        stringRedisTemplate.opsForValue().set(studentId,refreshToken,expirationTime, TimeUnit.MILLISECONDS);
    }

    //Refresh Token 조회하기
    public String getRefreshToken(String studentId) {
        return stringRedisTemplate.opsForValue().get(studentId);
    }

    //Refresh Token 삭제하기
    public void deleteRefreshToken(String studentId) {
        stringRedisTemplate.delete(studentId);
    }
}
