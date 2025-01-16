package com.keunsori.keunsoriserver.member;

import com.keunsori.keunsoriserver.member.Dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<List<MemberResponseDto>> findAll(){
        List<MemberResponseDto> memberList = memberService.findAll();
        return ResponseEntity.ok().body(memberList);
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
