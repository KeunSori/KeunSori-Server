package com.keunsori.keunsoriserver.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keunsori.keunsoriserver.reservation.dto.ReservationCreateRequest;
import com.keunsori.keunsoriserver.reservation.dto.ReservationResponse;
import com.keunsori.keunsoriserver.reservation.dto.ReservationUpdateRequest;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/list")
    public ResponseEntity<List<ReservationResponse>> findAllReservations(@RequestParam String yearMonth) {
        List<ReservationResponse> responses = reservationService.findReservationsByMonth(yearMonth);
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping
    public ResponseEntity<Void> createReservation(@RequestBody ReservationCreateRequest request) {
        reservationService.createReservation(request);
        return ResponseEntity.created(URI.create("")).build();
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservation(@PathVariable Long reservationId,
            @RequestBody ReservationUpdateRequest request) throws Exception {
        reservationService.updateReservation(reservationId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> findMyReservations() {
        List<ReservationResponse> responses = reservationService.findAllMyReservations();
        return ResponseEntity.ok().body(responses);
    }
}
