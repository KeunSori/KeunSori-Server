package com.keunsori.keunsoriserver.domain.admin.account.dto;

import com.keunsori.keunsoriserver.domain.member.domain.Member;

public record AdminMyPageResponse(
        String name,
        String studentId,
        String email
) {
    public static AdminMyPageResponse from(Member member){
        return new AdminMyPageResponse(
                member.getName(),
                member.getStudentId(),
                member.getEmail()
        );
    }
}
