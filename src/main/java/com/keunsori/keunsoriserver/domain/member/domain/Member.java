package com.keunsori.keunsoriserver.domain.member.domain;

import com.keunsori.keunsoriserver.domain.common.BaseEntity;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;


@Entity
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "student_id")
    private String studentId;

    private String hongikgmail;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String name;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
}
