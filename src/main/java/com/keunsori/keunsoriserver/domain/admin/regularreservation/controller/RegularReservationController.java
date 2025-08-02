package com.keunsori.keunsoriserver.domain.admin.regularreservation.controller;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request.RegularReservationCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.response.RegularReservationResponse;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.service.RegularReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<RegularReservationResponse>> createRegularReservation(@RequestBody List<@Valid RegularReservationCreateRequest> regularReservationCreateRequests) {
        List<RegularReservationResponse> responses = regularReservationService.createRegularReservations(regularReservationCreateRequests);
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRegularReservation(@RequestBody List<Long> regularReservationIds) {
        regularReservationService.deleteRegularReservation(regularReservationIds);
        return ResponseEntity.noContent().build();
    }

}
