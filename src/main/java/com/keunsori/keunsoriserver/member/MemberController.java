package com.keunsori.keunsoriserver.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private MemberService memberService;

}
