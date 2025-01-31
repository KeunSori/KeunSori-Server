package com.keunsori.keunsoriserver.domain.admin.controller;

import com.keunsori.keunsoriserver.domain.admin.dto.request.WeeklyScheduleRequest;
import com.keunsori.keunsoriserver.domain.admin.service.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservation")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    @PostMapping("/weekly-schedule")
    public ResponseEntity<Void> updateWeeklySchedule(@RequestBody List<WeeklyScheduleRequest> requests){
        adminReservationService.updateWeeklySchedule(requests);
        return ResponseEntity.ok().build();
    }
}
