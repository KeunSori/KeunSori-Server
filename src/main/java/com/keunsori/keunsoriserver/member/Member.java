package com.keunsori.keunsoriserver.member;

import com.keunsori.keunsoriserver.member.Dto.MemberRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void approve() {
        if ("APPROVED".equalsIgnoreCase(String.valueOf(this.status))) {
            throw new IllegalStateException("Member is already approved.");
        }
        this.status = MemberStatus.valueOf("일반");
    }
}
