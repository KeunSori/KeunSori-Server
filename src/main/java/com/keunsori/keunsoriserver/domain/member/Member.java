package com.keunsori.keunsoriserver.domain.member;

import jakarta.persistence.*;
import lombok.Getter;


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

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;



    protected Member() {}

    public Member(String studentId, String hongikgmail, String password, String name, MemberStatus status) {
        this.studentId = studentId;
        this.hongikgmail = hongikgmail;
        this.password = password;
        this.name = name;
        this.status = status;
    }

}
