package com.keunsori.keunsoriserver.domain.member.dto.response;

import com.keunsori.keunsoriserver.domain.member.domain.Member;

public record MyPageResponse(
        Long memberId,
        String name,
        String studentId,
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
