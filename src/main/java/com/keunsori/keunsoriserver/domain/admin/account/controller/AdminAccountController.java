package com.keunsori.keunsoriserver.domain.admin.account.controller;

import com.keunsori.keunsoriserver.domain.admin.account.dto.AdminMyPageResponse;
import com.keunsori.keunsoriserver.domain.admin.account.service.AdminAccountService;
import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<AdminMyPageResponse> getAdminMyPage(){
        AdminMyPageResponse response = adminAccountService.getAdminMyPage();
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody MemberPasswordUpdateRequest request){
        memberService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}
