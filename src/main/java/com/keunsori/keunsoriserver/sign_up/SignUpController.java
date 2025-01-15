package com.keunsori.keunsoriserver.sign_up;

import com.keunsori.keunsoriserver.sign_up.dto.SignUpRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<?> registerMember(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO, BindingResult bindingResult) {

        //유효성 검사 실패->에러
        if (bindingResult.hasErrors()) {
            StringBuilder errorMSG=new StringBuilder();
            bindingResult.getFieldErrors().forEach(error->{
                errorMSG.append(error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errorMSG.toString());
        }

        //password와 password confirm 일치 체크
        if(!signUpRequestDTO.getPassword().equals(signUpRequestDTO.getPasswordConfirm())){
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
        }

        //password=password confirm까지 수행된 후 멤버 엔티티 빌드 try
        try{
            signUpService.registerMember(signUpRequestDTO);
            return ResponseEntity.ok().build();
        }
        //이미 가입된 학번, 이메일->오류
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
