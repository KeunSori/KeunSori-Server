package com.keunsori.keunsoriserver.global.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    private static String HEADER ="Authorization";
    private static String PREFIX="Bearer ";

    public static final long ACCESS_TOKEN_VALIDITY_TIME=30 * 60 * 1000L;
    public static final long REFRESH_TOKEN_VALIDITY_TIME=7 * 24 * 60 * 60 * 1000L;

    public String getHeader() {
        return HEADER;
    }
    public String getPrefix() {
        return PREFIX;
    }
    public String getSecret() {
        return secret;
    }
}
