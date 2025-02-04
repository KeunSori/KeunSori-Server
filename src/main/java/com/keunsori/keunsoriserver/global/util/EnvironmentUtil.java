package com.keunsori.keunsoriserver.global.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnvironmentUtil {

    private final Environment environment;

    public Stream<String> getActiveProfiles() {
        return Stream.of(environment.getActiveProfiles());
    }
}
