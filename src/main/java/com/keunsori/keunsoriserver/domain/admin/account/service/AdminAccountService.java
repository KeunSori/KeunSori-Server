package com.keunsori.keunsoriserver.domain.admin.account.service;

import com.keunsori.keunsoriserver.domain.admin.account.dto.AdminMyPageResponse;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final MemberUtil memberUtil;

    public AdminMyPageResponse getAdminMyPage() {
        Member member = memberUtil.getLoggedInMember();
        return AdminMyPageResponse.from(member);
    }
}
