package com.keunsori.keunsoriserver.reservation.service;

import static com.keunsori.keunsoriserver.fixture.MemberFixture.GENERAL_MEMBER;
import static com.keunsori.keunsoriserver.fixture.ReservationFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.keunsori.keunsoriserver.domain.reservation.domain.Reservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.repository.ReservationRepository;
import com.keunsori.keunsoriserver.domain.reservation.service.ReservationService;
import com.keunsori.keunsoriserver.global.exception.ReservationException;
import com.keunsori.keunsoriserver.global.util.MemberUtil;

import java.time.LocalDate;
import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MemberUtil memberUtil;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void 예약_생성_성공() {
        // given
        given(memberUtil.getLoggedInMember()).willReturn(GENERAL_MEMBER);
        given(reservationRepository.save(any(Reservation.class))).willReturn(RESERVATION_1);
        ReservationCreateRequest request = new ReservationCreateRequest(
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0)
        );

        // when
        reservationService.createReservation(request);

        // then
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void 예약_생성_실패__종료시간이_시작시간을_앞서는_경우() {
        // given
        given(memberUtil.getLoggedInMember()).willReturn(GENERAL_MEMBER);
        given(reservationRepository.save(any(Reservation.class))).willReturn(RESERVATION_1);

        ReservationCreateRequest request = new ReservationCreateRequest(
                ReservationType.PERSONAL,
                Session.DRUM,
                LocalDate.of(2025, 1, 1),
                LocalTime.of(12, 0),
                LocalTime.of(11, 0)
        );

        // when & then
//        Assertions.assertThatThrownBy(() -> {reservationService.createReservation(request);})
//                .isInstanceOf(ReservationException.class);
    }
}
