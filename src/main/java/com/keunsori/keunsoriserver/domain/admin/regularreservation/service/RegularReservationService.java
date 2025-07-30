package com.keunsori.keunsoriserver.domain.admin.regularreservation.service;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.validator.RegularReservationValidator;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.response.RegularReservationResponse;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.repository.RegularReservationRepository;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.repository.MemberRepository;
import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.validator.ReservationValidator;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegularReservationService {

    private final RegularReservationRepository regularReservationRepository;
    private final RegularReservationValidator regularReservationValidator;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;
    private final ReservationValidator reservationValidator;

    // 정기 예약 생성
    @Transactional
    public Long createRegularReservation(RegularReservationCreateRequest regularReservationCreateRequest) {
        // 학번으로 멤버 조회
        Member member = memberRepository.findByStudentId(regularReservationCreateRequest.studentId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

        // 유효성 검증
        regularReservationValidator.validateCreateRegularReservation(regularReservationCreateRequest);

        // 예약 저장
        RegularReservation savedRegularReservation = regularReservationRepository.save(regularReservationCreateRequest.toEntity(member));

        generateDailyReservations(regularReservationCreateRequest, member, savedRegularReservation);

        return savedRegularReservation.getId();
    }

    // 정기 예약 삭제(팀장 or 관리자만 가능)
    @Transactional
    public void deleteRegularReservation(List<Long> regularReservationIds) {
        Member loginMember = memberUtil.getLoggedInMember();

        List<RegularReservation> regularReservations = regularReservationValidator.validateAndGetAllExists(regularReservationIds);

        for (RegularReservation regularReservation : regularReservations) {
            regularReservationValidator.validateDeletable(regularReservation, loginMember);

            // 연결된 일간 예약도 같이 삭제
            reservationRepository.deleteAllByRegularReservation(regularReservation);
        }

        regularReservationRepository.deleteAll(regularReservations);
    }

    // 정기예약 기간에 따른 일간 예약 반복 생성
    private void generateDailyReservations(RegularReservationCreateRequest regularReservationCreateRequest, Member member, RegularReservation savedRegularReservation) {
        LocalDate current = regularReservationCreateRequest.applyStartDate();
        while (!current.isAfter(regularReservationCreateRequest.applyEndDate())) {
            if (current.getDayOfWeek().equals(regularReservationCreateRequest.dayOfWeek())) {
                ReservationCreateRequest dto = new ReservationCreateRequest(
                        regularReservationCreateRequest.regularReservationType(), regularReservationCreateRequest.regularReservationSession(), current,
                        regularReservationCreateRequest.regularReservationStartTime(), regularReservationCreateRequest.regularReservationEndTime());

                reservationValidator.validateReservationCreateRequest(dto);

                Reservation reservation = Reservation.builder()
                        .reservationType(ReservationType.TEAM)
                        .session(Session.ALL)
                        .date(current)
                        .startTime(regularReservationCreateRequest.regularReservationStartTime())
                        .endTime(regularReservationCreateRequest.regularReservationEndTime())
                        .member(member)
                        .regularReservation(savedRegularReservation)
                        .build();

                reservationRepository.save(reservation);
            }
            current = current.plusDays(1);
        }
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

}
