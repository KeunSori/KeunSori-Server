package com.keunsori.keunsoriserver.member.Dto;

import com.keunsori.keunsoriserver.member.Member;
import com.keunsori.keunsoriserver.member.MemberStatus;

import java.time.LocalDateTime;

public record MemberApprovalResponse(
        Long id,
        String name,
        String StudentId,
        MemberStatus status,
        LocalDateTime SubsriptionDate
) {

    public static MemberResponse fromEntity(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getStudentId(),
                member.getStatus(),
                member.getApprovalDate() //createDate로 바꿔야함
        );
    }

}
