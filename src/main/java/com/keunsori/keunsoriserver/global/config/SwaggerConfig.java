package com.keunsori.keunsoriserver.global.config;

import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.DEV_SERVER_URL;
import static com.keunsori.keunsoriserver.global.constant.EnvironmentConstant.LOCAL_SERVER_URL;
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
import org.springframework.context.annotation.Profile;

import com.keunsori.keunsoriserver.global.util.EnvironmentUtil;

@Configuration
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class SwaggerConfig {

    private final EnvironmentUtil environmentUtil;

    @Bean
    public OpenAPI openAPI() {
        String activeProfile = environmentUtil.getCurrentProfile();

        Server server = new Server();
        if (activeProfile.equalsIgnoreCase("dev")) {
            server.setUrl(DEV_SERVER_URL);
        }

        if (activeProfile.equalsIgnoreCase("local")) {
            server.setUrl(LOCAL_SERVER_URL);
        }

        return new OpenAPI()
                .servers(List.of(server))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .addSecurityItem(new SecurityRequirement().addList("accessToken"))
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
                                .name(AUTHORIZATION)
                )
                .addSecuritySchemes(
                        "Access-Token",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("Access-Token")
                );
    }
}
