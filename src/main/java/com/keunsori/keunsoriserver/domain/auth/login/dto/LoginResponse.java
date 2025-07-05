package com.keunsori.keunsoriserver.domain.auth.login.dto;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;

public record LoginResponse (
    String name,
    String studentId,
    MemberStatus status
)
{
    public static LoginResponse from(Member member){
        return new LoginResponse(
                member.getName(),
                member.getStudentId(),
                member.getStatus()
        );
    }
}

