package com.keunsori.keunsoriserver.domain.member.sign_up.controller;

import com.keunsori.keunsoriserver.domain.member.sign_up.dto.request.SignUpRequest;
import com.keunsori.keunsoriserver.domain.member.sign_up.dto.response.SignUpResponse;
import com.keunsori.keunsoriserver.domain.member.sign_up.service.SignUpService;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
public class SignUpController {

    private final SignUpService signUpService;

    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping
    public ResponseEntity<SignUpResponse> registerMember(@Valid @RequestBody SignUpRequest signUpRequest) {

        //멤버 엔티티 빌드
        SignUpResponse response=signUpService.registerMember(signUpRequest);
        return ResponseEntity.ok(response);

    }
}
