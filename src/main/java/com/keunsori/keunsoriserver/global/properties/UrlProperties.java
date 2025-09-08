package com.keunsori.keunsoriserver.global.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlProperties {
    @Value("${url.domain}")
    private String serverDomain;

    public String getPasswordChangePath() {
        return serverDomain + "password/change";
    }
}
