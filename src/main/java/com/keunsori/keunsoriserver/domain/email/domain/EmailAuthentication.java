package com.keunsori.keunsoriserver.domain.email.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.keunsori.keunsoriserver.global.exception.EmailException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.keunsori.keunsoriserver.global.exception.ErrorCode.*;

@Getter
@RedisHash(value = "emailAuthentication", timeToLive = 300)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuthentication {

    @Id
    private String email;

    private String authNumber;

    public EmailAuthentication(String email, String authNumber) {
        this.email = email;
        this.authNumber = authNumber;
    }

    public static EmailAuthentication of(String email, String authNumber) {
        return new EmailAuthentication(email, authNumber);
    }

    public void verifyAuthNumber(String authNumber) {
        if (!authNumber.equals(this.authNumber)) {
            throw new EmailException(EMAIL_VERIFY_FAILED);
        }
    }
}
