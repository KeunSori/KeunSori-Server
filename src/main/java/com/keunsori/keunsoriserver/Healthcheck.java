package com.keunsori.keunsoriserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Healthcheck {

    @GetMapping("/healthcheck")
    public ResponseEntity healthcheck(){
        return ResponseEntity.ok().body("Good");
    }
}
