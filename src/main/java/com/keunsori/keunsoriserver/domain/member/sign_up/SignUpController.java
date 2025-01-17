package com.keunsori.keunsoriserver.domain.member.sign_up;

import com.keunsori.keunsoriserver.domain.member.sign_up.dto.SignUpRequest;
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
    public ResponseEntity<?> registerMember(@Valid @RequestBody SignUpRequest signUpRequest) throws MemberException.IncorrectPasswordException, MemberException.InvalidHongikGmailException, MemberException.InvalidStudentIdException {

        //멤버 엔티티 빌드
        signUpService.registerMember(signUpRequest);
        return ResponseEntity.ok("회원가입 완료");

    }
}
