package com.keunsori.keunsoriserver.domain.email.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keunsori.keunsoriserver.domain.email.dto.request.AuthNumberSendRequest;
import com.keunsori.keunsoriserver.domain.email.dto.request.AuthNumberVerifyRequest;
import com.keunsori.keunsoriserver.domain.email.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/auth-number/send")
    public ResponseEntity<Void> sendAuthNumber(@Valid @RequestBody AuthNumberSendRequest request) {
        emailService.sendAuthNumber(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth-number/verify")
    public ResponseEntity<Void> validateAuthNumber(@Valid @RequestBody AuthNumberVerifyRequest request) {
        emailService.verifyAuthNumber(request);
        return ResponseEntity.ok().build();
    }
}
