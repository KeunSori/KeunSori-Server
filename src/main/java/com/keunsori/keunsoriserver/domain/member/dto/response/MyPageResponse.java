package com.keunsori.keunsoriserver.domain.member.dto.response;

import com.keunsori.keunsoriserver.domain.member.domain.Member;

public record MyPageResponse(
        String name,
        String studentId,
        String email,
        String status
) {
    public static MyPageResponse from(Member member){
        return new MyPageResponse(
                member.getName(),
                member.getStudentId(),
                member.getEmail(),
                member.getStatus().toString()
        );
    }
}
