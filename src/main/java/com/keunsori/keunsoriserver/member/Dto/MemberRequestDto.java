package com.keunsori.keunsoriserver.member.Dto;

import com.keunsori.keunsoriserver.member.MemberStatus;
import lombok.Getter;

@Getter
public class MemberRequestDto {
    private Long id;
    private String StudentId;
    private String gmail;
    private String password;
    private String password2;
    private String name;

    public void validatePasswordMatch() {
        if (!password.equals(password2)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }
}
