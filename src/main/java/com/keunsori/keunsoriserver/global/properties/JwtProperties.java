package com.keunsori.keunsoriserver.global.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {
    @Value("${jwt.secret}")
    private String secret;

    public static final String HEADER ="Authorization";
    public static final String PREFIX="Bearer ";

    public static final long ACCESS_TOKEN_VALIDITY_TIME=30 * 60 * 1000L;
    public static final long REFRESH_TOKEN_VALIDITY_TIME=7 * 24 * 60 * 60 * 1000L;

    public String getSecret() {
        return secret;
    }
}
