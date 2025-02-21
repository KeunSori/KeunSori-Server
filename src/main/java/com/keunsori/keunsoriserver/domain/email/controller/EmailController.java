package com.keunsori.keunsoriserver.domain.email.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keunsori.keunsoriserver.domain.email.dto.request.EmailValidateRequest;
import com.keunsori.keunsoriserver.domain.email.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send/auth-number")
    public ResponseEntity<Void> validateEmail(@RequestBody EmailValidateRequest request) {
        emailService.sendAuthNumber(request);
        return ResponseEntity.ok().build();
    }

}
