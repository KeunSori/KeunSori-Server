package com.keunsori.keunsoriserver.domain.admin.regularreservation.service;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.validator.RegularReservationValidator;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.response.RegularReservationResponse;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.repository.RegularReservationRepository;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.exception.RegularReservationException;
import com.keunsori.keunsoriserver.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.MEMBER_NOT_EXISTS_WITH_STUDENT_ID;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.REGULAR_RESERVATION_NOT_EXISTS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegularReservationService {

    private final RegularReservationRepository regularReservationRepository;
    private final RegularReservationValidator regularReservationValidator;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;

    // 정기 예약 생성
    @Transactional
    public Long createRegularReservation(RegularReservationCreateRequest regularReservationCreateRequest) {
        // 학번으로 멤버 조회
        Member member = memberRepository.findByStudentId(regularReservationCreateRequest.studentId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        // 유효성 검증
        regularReservationValidator.validateCreateRegularReservation(regularReservationCreateRequest);

        // 3. 예약 저장
        RegularReservation savedRegularReservation = regularReservationRepository.save(regularReservationCreateRequest.toEntity(member));

        return savedRegularReservation.getId();
    }

    // 정기 예약 삭제(팀장 or 관리자만 가능)
    @Transactional
    public void deleteRegularReservation(List<Long> regularReservationIds) {
        Member loginMember = memberUtil.getLoggedInMember();

        List<RegularReservation> regularReservations = regularReservationRepository.findAllById(regularReservationIds);

        // 요청 정기 예약 ID 수 != 조회된 정기 예약 아이템 수 -> 일부 예약 아이템이 존재하지 않는다.
        if(regularReservations.size() != regularReservationIds.size()){
            throw new RegularReservationException(REGULAR_RESERVATION_NOT_EXISTS);
        }

        for (RegularReservation regularReservation : regularReservations) {
            regularReservationValidator.validateDeletable(regularReservation, loginMember);
        }

        regularReservationRepository.deleteAll(regularReservations);
    }

    public List<RegularReservationResponse> findAllRegularReservations() {
        Member loginMember = memberUtil.getLoggedInMember();

        if(loginMember.isAdmin()){
            return regularReservationRepository.findAllByOrderByDayOfWeekAscStartTimeAsc()
                    .stream()
                    .map(RegularReservationResponse::from)
                    .toList();
        }

        return regularReservationRepository.findAllByMember_StudentId(loginMember.getStudentId())
                .stream()
                .map(RegularReservationResponse::from)
                .toList();
    }

    // 요일별 정기예약 조회
    public List<RegularReservationResponse> findRegularReservationsByDayOfWeek(DayOfWeek dayOfWeek) {
        return regularReservationRepository.findAllByDayOfWeek(dayOfWeek)
                .stream()
                .map(RegularReservationResponse::from)
                .toList();
    }
}
