package com.keunsori.keunsoriserver.domain.email.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "emailAuthentication", timeToLive = 300)
public class EmailAuthentication {

    @Id
    private final String email;

    private final String authNumber;

    public EmailAuthentication(String email, String authNumber) {
        this.email = email;
        this.authNumber = authNumber;
    }

    public static EmailAuthentication of(String email, String authNumber) {
        return new EmailAuthentication(email, authNumber);
    }
}
