package com.keunsori.keunsoriserver.domain.admin.controller;

import com.keunsori.keunsoriserver.domain.admin.dto.request.WeeklyScheduleRequest;
import com.keunsori.keunsoriserver.domain.admin.dto.response.AvailableDateTimeResponse;
import com.keunsori.keunsoriserver.domain.admin.dto.response.WeeklyScheduleResponse;
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

    // URL 설정 필요
    @GetMapping
    public ResponseEntity<List<AvailableDateTimeResponse>> findAllAvailableDateTimes(@RequestParam("month") String month){
        List<AvailableDateTimeResponse> responses = adminReservationService.findAllAvailableDateTimes(month);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/weekly-schedule")
    public ResponseEntity<List<WeeklyScheduleResponse>> findAllWeeklySchedules(){
        List<WeeklyScheduleResponse> responses = adminReservationService.findAllWeeklySchedules();
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping("/weekly-schedule")
    public ResponseEntity<Void> updateWeeklySchedule(@RequestBody List<WeeklyScheduleRequest> requests){
        adminReservationService.updateWeeklySchedule(requests);
        return ResponseEntity.ok().build();
    }
}
