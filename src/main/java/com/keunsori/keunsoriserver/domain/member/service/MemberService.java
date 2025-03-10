package com.keunsori.keunsoriserver.domain.member.service;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.dto.request.MemberPasswordUpdateRequest;
import com.keunsori.keunsoriserver.domain.member.dto.response.MyPageResponse;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.AuthException;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final MemberUtil memberUtil;
    private final PasswordEncoder passwordEncoder;

    public MyPageResponse getMyPage() {
        Member member = memberUtil.getLoggedInMember();
        return MyPageResponse.from(member);
    }

    @Transactional
    public void updatePassword(MemberPasswordUpdateRequest request) {
        Member member = memberUtil.getLoggedInMember();

        if(!passwordEncoder.matches(request.currentPassword(), member.getPassword())){
            throw new AuthException(PASSWORD_NOT_CORRECT);
        }

        if(!request.newPassword().equals(request.passwordConfirm())) {
            throw new MemberException(PASSWORD_IS_DIFFERENT_FROM_CHECK);
        }

        String encodedPassword = passwordEncoder.encode(request.newPassword());
        member.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        // 회원과 연결된 예약의 외래 키를 null로 설정
        reservationRepository.unlinkMember(id);

        memberRepository.delete(member);
    }
}
