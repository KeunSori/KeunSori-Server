package com.keunsori.keunsoriserver.domain.member.controller;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.dto.MemberApprovalResponse;
import com.keunsori.keunsoriserver.domain.member.dto.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/members")
public class AdminMemberController {
    private MemberService memberService;

    // 회원관리 리스트
    @GetMapping("/list")
    public ResponseEntity<List<MemberResponse>> findAllMember(){
        List<MemberResponse> memberList = memberService.findAllMember();
        return ResponseEntity.ok().body(memberList);
    }

    // 가입승인 리스트
    @GetMapping("/approve")
    public ResponseEntity<List<MemberApprovalResponse>> findAllWaiting(){
        List<MemberApprovalResponse> waitingList = memberService.findAllWaiting();
        return ResponseEntity.ok().body(waitingList);
    }

    // 가입승인
    @PatchMapping("/approve/{id}")
    public ResponseEntity<Void> approveMember(@PathVariable Long id){
        memberService.approveMember(id);
        return ResponseEntity.ok().build();
    }

    // 회원관리 - 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberByAdmin(@PathVariable Long id){
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
