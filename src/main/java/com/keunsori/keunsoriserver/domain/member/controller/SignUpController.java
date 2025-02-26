package com.keunsori.keunsoriserver.domain.member.controller;

import com.keunsori.keunsoriserver.domain.member.dto.request.SignUpRequest;
import com.keunsori.keunsoriserver.domain.member.dto.response.SignUpResponse;
import com.keunsori.keunsoriserver.domain.member.service.SignUpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<SignUpResponse> registerMember(@Valid @RequestBody SignUpRequest signUpRequest) {

        //멤버 엔티티 빌드
        SignUpResponse response = signUpService.registerMember(signUpRequest);
        return ResponseEntity.ok(response);
    }
}
