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
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import com.keunsori.keunsoriserver.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<RegularReservationResponse> createRegularReservations(List<RegularReservationCreateRequest> regularReservationCreateRequests) {
        List<RegularReservationResponse> responses = new ArrayList<>();

        for (RegularReservationCreateRequest regularReservationCreateRequest : regularReservationCreateRequests) {
            // 학번으로 멤버 조회
            Member teamLeader = memberRepository.findByStudentId(regularReservationCreateRequest.studentId())
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_EXISTS_WITH_STUDENT_ID));

            // 유효성 검증
            regularReservationValidator.validateCreateRegularReservation(regularReservationCreateRequest);

            // 정기 예약 저장
            RegularReservation savedRegularReservation = regularReservationRepository.save(regularReservationCreateRequest.toEntity(teamLeader));

            // 일간 예약 저장
            generateDailyReservations(savedRegularReservation);

            responses.add(RegularReservationResponse.from(savedRegularReservation));
        }
        return responses;
    }

    // 정기 예약 삭제(관리자만 가능)
    @Transactional
    public void deleteRegularReservation(List<Long> regularReservationIds) {
        Member loginMember = memberUtil.getLoggedInMember();

        regularReservationValidator.validateDeletable(loginMember);

        List<RegularReservation> regularReservations = regularReservationRepository.findAllById(regularReservationIds);

        regularReservationValidator.validateAndGetAllExists(regularReservationIds, regularReservations);

        for (RegularReservation regularReservation : regularReservations) {
            // 연결된 일간 예약 같이 삭제
            reservationRepository.deleteAllByRegularReservation(regularReservation);
        }
        regularReservationRepository.deleteAll(regularReservations);
    }

    // 정기예약 기간에 따른 일간 예약 반복 생성
    private void generateDailyReservations(RegularReservation savedRegularReservation) {
        LocalDate reservationDate = savedRegularReservation.getApplyStartDate();
        while (!reservationDate.isAfter(savedRegularReservation.getApplyEndDate())) {
            if (reservationDate.getDayOfWeek().equals(savedRegularReservation.getDayOfWeek())) {
                ReservationCreateRequest dto = new ReservationCreateRequest(
                        savedRegularReservation.getReservationType().name(), savedRegularReservation.getSession().name(), reservationDate,
                        savedRegularReservation.getStartTime(), savedRegularReservation.getEndTime());

                reservationValidator.validateReservationCreateRequest(dto);

                Reservation reservation = Reservation.builder()
                        .reservationType(savedRegularReservation.getReservationType())
                        .session(savedRegularReservation.getSession())
                        .date(reservationDate)
                        .startTime(savedRegularReservation.getStartTime())
                        .endTime(savedRegularReservation.getEndTime())
                        .member(savedRegularReservation.getMember())
                        .regularReservation(savedRegularReservation)
                        .build();

                reservationRepository.save(reservation);
            }
            reservationDate = reservationDate.plusDays(7);
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
