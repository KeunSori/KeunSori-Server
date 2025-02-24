package com.keunsori.keunsoriserver.domain.member.dto.response;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.domain.Member;

import java.time.LocalDateTime;

public record MemberResponse(
    Long id,
    String name,
    String StudentId,
    LocalDateTime approvalDate
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getStudentId(),
                member.getApprovalDate()
        );
    }
}

