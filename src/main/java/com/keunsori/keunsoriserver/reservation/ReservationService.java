package com.keunsori.keunsoriserver.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keunsori.keunsoriserver.member.Member;
import com.keunsori.keunsoriserver.reservation.dto.ReservationCreateRequest;
import com.keunsori.keunsoriserver.reservation.dto.ReservationResponse;
import com.keunsori.keunsoriserver.reservation.dto.ReservationUpdateRequest;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationResponse> findAllReservations() {
        return reservationRepository.findAll().stream().map(ReservationResponse::of).toList();
    }

    @Transactional
    public void createReservation(ReservationCreateRequest request) {
        Reservation reservation = request.toEntity(null); // TODO : 로그인 구현 이후 현재 로그인한 멤버 조회
        reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        // TODO : 현재 로그인 유저의 예약인지 또는 현재 유저가 관리자인지 검증
        reservationRepository.deleteById(reservationId);
    }

    @Transactional
    public void updateReservation(Long reservationId, ReservationUpdateRequest request)
            throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("존재하지 않는 예약 ID 입니다."));
        // TODO : 에러 메세지 클래스 작성, 커스텀 에러 클래스 작성

        // TODO : 현재 로그인한 유저의 예약인지 검증
        // TODO : 과거 예약인지 검증 (어노테이션 이용)

        reservation.updateReservation(
                request.reservationType(),
                request.reservationSession(),
                request.reservationDate(),
                request.reservationStartTime(),
                request.reservationEndTime()
        );
    }

    public List<ReservationResponse> findAllMyReservations() {
        Member currentMember = new Member(); // TODO : 로그인 유저 가져오도록 변경
        return reservationRepository.findAllByMember(currentMember)
                .stream().map(ReservationResponse::of).toList();
    }
}
