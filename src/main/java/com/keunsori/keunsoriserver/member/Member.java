package com.keunsori.keunsoriserver.member;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String studentId;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String name;
}
