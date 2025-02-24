package com.keunsori.keunsoriserver.domain.member.dto.response;

import com.keunsori.keunsoriserver.domain.member.domain.Member;

public record MyPageResponse(
        Long id,
        String name,
        String StudentId,
        String email
) {
    public static MyPageResponse from(Member member){
        return new MyPageResponse(
                member.getId(),
                member.getName(),
                member.getStudentId(),
                member.getEmail()
        );
    }
}
