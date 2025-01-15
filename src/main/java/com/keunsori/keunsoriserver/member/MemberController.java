package com.keunsori.keunsoriserver.member;

import com.keunsori.keunsoriserver.member.Dto.MemberRequestDto;
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

    @GetMapping()
    public ResponseEntity<List<MemberResponseDto>> findAll(){
        List<MemberResponseDto> memberList = memberService.findAll();
        return ResponseEntity.ok(memberList);
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<Member> approveMember(@PathVariable Long id){
        memberService.approveMember(id);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMember(@PathVariable Long id){
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok("Member deleted");
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
