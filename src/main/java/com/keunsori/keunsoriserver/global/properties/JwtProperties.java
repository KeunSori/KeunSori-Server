package com.keunsori.keunsoriserver.global.properties;

import org.springframework.stereotype.Component;

@Component
public class JwtProperties {
    public static final long ACCESS_TOKEN_VALIDITY_TIME=30 * 60 * 1000L;
    public static final long REFRESH_TOKEN_VALIDITY_TIME=7 * 24 * 60 * 60 * 1000L;
}
