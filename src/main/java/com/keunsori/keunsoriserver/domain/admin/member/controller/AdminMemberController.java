package com.keunsori.keunsoriserver.domain.admin.member.controller;

import com.keunsori.keunsoriserver.domain.admin.member.dto.response.MemberApplicantResponse;
import com.keunsori.keunsoriserver.domain.admin.member.service.AdminMemberService;
import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.member.dto.response.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.dto.response.MyPageResponse;
import com.keunsori.keunsoriserver.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;
    private final MemberService memberService;

    // 회원관리 리스트
    @GetMapping("/list")
    public ResponseEntity<List<MemberResponse>> findAllMembers(){
        List<MemberResponse> memberList = adminMemberService.findAllMember();
        return ResponseEntity.ok().body(memberList);
    }

    // 가입승인 리스트
    @GetMapping("/applicants")
    public ResponseEntity<List<MemberApplicantResponse>> findAllApplicants(){
        List<MemberApplicantResponse> applicants = adminMemberService.findAllApplicants();
        return ResponseEntity.ok().body(applicants);
    }

    // 가입승인
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveMember(@PathVariable("id") Long id){
        adminMemberService.approveMember(id);
        return ResponseEntity.ok().build();
    }

    // 회원관리 - 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberByAdmin(@PathVariable("id") Long id){
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MyPageResponse> getMyPage() {
        MyPageResponse response = memberService.getMyPage();
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody MemberPasswordUpdateRequest request){
        memberService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}
