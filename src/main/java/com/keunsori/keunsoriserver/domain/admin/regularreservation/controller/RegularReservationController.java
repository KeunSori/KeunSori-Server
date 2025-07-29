package com.keunsori.keunsoriserver.domain.admin.regularreservation.controller;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.response.RegularReservationResponse;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.service.RegularReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin/regular-reservation")
@RequiredArgsConstructor
public class RegularReservationController {

    private final RegularReservationService regularReservationService;

    @GetMapping
    public ResponseEntity<List<RegularReservationResponse>> getRegularReservations() {
        List<RegularReservationResponse> regularReservationResponses = regularReservationService.findAllRegularReservations();
        return ResponseEntity.ok().body(regularReservationResponses);
    }

    @PostMapping
    public ResponseEntity<Void> createRegularReservation(@RequestBody @Valid RegularReservationCreateRequest regularReservationCreateRequest) {
        Long savedId = regularReservationService.createRegularReservation(regularReservationCreateRequest);
        return ResponseEntity.created(URI.create("/admin/regular-reservation/" + savedId)).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRegularReservation(@RequestParam List<Long> regularReservationIds) {
        regularReservationService.deleteRegularReservation(regularReservationIds);
        return ResponseEntity.noContent().build();
    }

}
