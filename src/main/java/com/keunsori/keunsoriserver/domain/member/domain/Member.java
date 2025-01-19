package com.keunsori.keunsoriserver.domain.member.domain;

import com.keunsori.keunsoriserver.domain.common.BaseEntity;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 7)
    private String studentId;

    @Column(length = 50)
    private String hongikgmail;

    @Column(length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberStatus status;

    @Column(length = 20)
    private String name;

    private LocalDateTime approvalDate;

    public Member(String studentId, String hongikgmail, String password, String name, MemberStatus status) {
        this.studentId = studentId;
        this.hongikgmail = hongikgmail;
        this.password = password;
        this.name = name;
        this.status = status;
    }
}
