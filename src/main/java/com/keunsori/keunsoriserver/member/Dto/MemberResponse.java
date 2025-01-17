package com.keunsori.keunsoriserver.member.Dto;

import com.keunsori.keunsoriserver.member.Member;
import com.keunsori.keunsoriserver.member.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public record MemberResponse(
    Long id,
    String name,
    String StudentId,
    MemberStatus status,
    LocalDateTime approvalDate
) {

    public static MemberResponse fromEntity(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getStudentId(),
                member.getStatus(),
                member.getApprovalDate()
        );
    }
}

