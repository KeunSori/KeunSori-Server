package com.keunsori.keunsoriserver.global.config;

import static org.springframework.http.HttpHeaders.*;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.keunsori.keunsoriserver.global.properties.SwaggerProperties;
import com.keunsori.keunsoriserver.global.util.EnvironmentUtil;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final EnvironmentUtil environmentUtil;

    @Bean
    public OpenAPI openAPI() {
        String activeProfile = environmentUtil.getActiveProfile();

        Server server = new Server();
        if (activeProfile.equalsIgnoreCase("dev")) {
            server.setUrl(SwaggerProperties.DEV_SERVER_URL);
        }

        if (activeProfile.equalsIgnoreCase("local")) {
            server.setUrl(SwaggerProperties.LOCAL_SERVER_URL);
        }

        return new OpenAPI()
                .servers(List.of(server))
                .addSecurityItem(securityRequirement())
                .components(authSetting());
    }

    private Components authSetting() {
        return new Components()
                .addSecuritySchemes(
                        "Authorization",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name(AUTHORIZATION));
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(AUTHORIZATION);
    }
}
