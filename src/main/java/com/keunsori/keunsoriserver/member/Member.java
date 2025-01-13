package com.keunsori.keunsoriserver.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String studentId;
    private String hongikgmail;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String name;

}
