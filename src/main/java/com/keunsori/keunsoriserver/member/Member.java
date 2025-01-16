package com.keunsori.keunsoriserver.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name="student_id")
    private String studentId;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String name;

    private LocalDateTime approvalDate;

    public void approve() {
        if (this.status == MemberStatus.일반) {
            throw new IllegalStateException("이미 가입이 승인된 회원입니다.");
        }
        this.status = MemberStatus.일반;
    }
}
