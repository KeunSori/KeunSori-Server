package com.keunsori.keunsoriserver.member.Dto;

import com.keunsori.keunsoriserver.member.Member;
import com.keunsori.keunsoriserver.member.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@Getter
public class MemberResponseDto {
    private Long id;
    private String StudentId;
    private MemberStatus status;
    private LocalDateTime approvalDate;

    public static MemberResponseDto fromEntity(Member member){
        return new MemberResponseDto(
                member.getId(),
                member.getStudentId(),
                member.getStatus(),
                member.getApprovalDate()
        );
    }
}
