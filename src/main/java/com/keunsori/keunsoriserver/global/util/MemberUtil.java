package com.keunsori.keunsoriserver.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;

import lombok.RequiredArgsConstructor;

import static com.keunsori.keunsoriserver.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getLoggedInMember() {
        return memberRepository.findByStudentId(getLoggedInMemberStudentId()).orElseThrow(
                () -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID)
        );
    }

    private String getLoggedInMemberStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
