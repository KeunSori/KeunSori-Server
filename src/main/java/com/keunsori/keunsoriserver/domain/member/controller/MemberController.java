package com.keunsori.keunsoriserver.domain.member.controller;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.dto.MemberApprovalResponse;
import com.keunsori.keunsoriserver.domain.member.dto.MemberResponse;
import com.keunsori.keunsoriserver.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<List<MemberResponse>> findAllMember(){
        List<MemberResponse> memberList = memberService.findAllMember();
        return ResponseEntity.ok().body(memberList);
    }

    @GetMapping("/approve")
    public ResponseEntity<List<MemberApprovalResponse>> findAllWaiting(){
        List<MemberApprovalResponse> waitingList = memberService.findAllWaiting();
        return ResponseEntity.ok().body(waitingList);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<Member> approveMember(@PathVariable Long id){
        Member member = memberService.approveMember(id);
        return ResponseEntity.ok().body(member);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMember(@PathVariable Long id){
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
