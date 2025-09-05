package com.keunsori.keunsoriserver.admin.member.fixture;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;

public class MemberFixture {

    public static Member ADMIN() {
        return createMember(
                "A000001",
                "testAdmin@g.hongik.ac.kr",
                "testadmin123!",
                MemberStatus.관리자
        );
    }

    public static Member GENERAL1() {
        return createMember(
                "C000001",
                "test@example.com",
                "test123!",
                MemberStatus.일반
        );
    }

    private static Member createMember(String studentId, String email, String password, MemberStatus memberStatus) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return Member.builder()
                .studentId(studentId)
                .email(email)
                .password(passwordEncoder.encode(password))
                .status(memberStatus)
                .build();
    }
}
