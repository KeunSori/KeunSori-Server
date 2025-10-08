package com.keunsori.keunsoriserver.domain.member.controller;

import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.member.dto.response.MyPageResponse;
import com.keunsori.keunsoriserver.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MyPageResponse> getMyPage() {
        MyPageResponse response = memberService.getMyPage();
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody MemberPasswordUpdateRequest request) {
        memberService.updatePassword(request);
        return ResponseEntity.ok().build();
    }

    asfdasfsafasdf
}
