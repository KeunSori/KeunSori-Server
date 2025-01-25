package com.keunsori.keunsoriserver.domain.member.dto.response;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.domain.Member;

import java.time.LocalDateTime;

public record MemberApprovalResponse(
        Long id,
        String name,
        String StudentId,
        MemberStatus status,
        LocalDateTime applicationDate
) {

    public static MemberApprovalResponse from(Member member) {
        return new MemberApprovalResponse(
                member.getId(),
                member.getName(),
                member.getStudentId(),
                member.getStatus(),
                member.getCreateDate()
        );
    }

}
