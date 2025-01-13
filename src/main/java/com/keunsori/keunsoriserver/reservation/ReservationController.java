package com.keunsori.keunsoriserver.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keunsori.keunsoriserver.reservation.dto.ReservationResponse;

import java.net.URI;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/list")
    public ResponseEntity<ReservationResponse> findAllReservations() {
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> createReservation() {
        return ResponseEntity.created(URI.create("")).build();
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
        System.out.println(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservation(@PathVariable Long reservationId) {
        System.out.println(reservationId);
        return ResponseEntity.ok().build();
    }
}
