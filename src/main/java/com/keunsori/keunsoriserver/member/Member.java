package com.keunsori.keunsoriserver.member;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String studentId;
    private String hongikgmail;

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
