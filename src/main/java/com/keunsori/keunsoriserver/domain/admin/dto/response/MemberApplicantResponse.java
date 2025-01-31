package com.keunsori.keunsoriserver.domain.admin.dto.response;

import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.domain.member.domain.Member;

import java.time.LocalDateTime;

public record MemberApplicantResponse(
        Long id,
        String name,
        String StudentId,
        MemberStatus status,
        LocalDateTime applicationDate
) {

    public static MemberApplicantResponse from(Member member) {
        return new MemberApplicantResponse(
                member.getId(),
                member.getName(),
                member.getStudentId(),
                member.getStatus(),
                member.getCreateDate()
        );
    }

}
