package com.keunsori.keunsoriserver.domain.admin.controller;

import com.keunsori.keunsoriserver.domain.member.dto.response.MemberApprovalResponse;
import com.keunsori.keunsoriserver.domain.member.dto.response.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    // 회원관리 리스트
    @GetMapping("/list")
    public ResponseEntity<List<MemberResponse>> findAllMember(){
        List<MemberResponse> memberList = memberService.findAllMember();
        return ResponseEntity.ok().body(memberList);
    }

    // 가입승인 리스트
    @GetMapping("/applicants")
    public ResponseEntity<List<MemberApprovalResponse>> findAllApplicants(){
        List<MemberApprovalResponse> waitingList = memberService.findAllApplicants();
        return ResponseEntity.ok().body(waitingList);
    }

    // 가입승인
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveMember(@PathVariable("id") Long id){
        memberService.approveMember(id);
        return ResponseEntity.ok().build();
    }

    // 회원관리 - 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberByAdmin(@PathVariable("id") Long id){
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
