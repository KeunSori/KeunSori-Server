package com.keunsori.keunsoriserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KeunSoriServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeunSoriServerApplication.class, args);
    }

}
