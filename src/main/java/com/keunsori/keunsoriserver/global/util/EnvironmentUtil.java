package com.keunsori.keunsoriserver.global.util;

import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtil {

    public String getActiveProfile() {
        return System.getProperty("spring.profiles.active");
    }
}
