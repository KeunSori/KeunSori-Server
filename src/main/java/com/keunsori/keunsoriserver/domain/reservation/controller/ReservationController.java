package com.keunsori.keunsoriserver.domain.reservation.controller;

import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.MonthlyScheduleResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationUpdateRequest;
import com.keunsori.keunsoriserver.domain.reservation.service.ReservationService;
import com.keunsori.keunsoriserver.domain.reservation.dto.requset.ReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.reservation.dto.response.ReservationResponse;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/list")
    public ResponseEntity<List<ReservationResponse>> findAllReservations(@RequestParam("month") String month) {
        List<ReservationResponse> responses = reservationService.findReservationsByMonth(month);
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping
    public ResponseEntity<Void> createReservation(@RequestBody @Valid ReservationCreateRequest request) {
        Long reservationId = reservationService.createReservation(request);
        return ResponseEntity.created(URI.create("/reservation/" + reservationId)).build();
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable(name = "reservationId") Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservation(@PathVariable(name = "reservationId") Long reservationId,
            @RequestBody @Valid ReservationUpdateRequest request) {
        reservationService.updateReservation(reservationId, request);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> findMyReservations() {
        List<ReservationResponse> responses = reservationService.findAllMyReservations();
        return ResponseEntity.ok().body(responses);
    }

    // 예약 신청 페이지 가능한 날짜 반환
    @GetMapping
    public ResponseEntity<MonthlyScheduleResponse> findMonthlySchedule(@RequestParam("month") String month){
        MonthlyScheduleResponse response = reservationService.findMonthlySchedule(month);
        return ResponseEntity.ok().body(response);
    }
}
