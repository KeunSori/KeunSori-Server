package com.keunsori.keunsoriserver.domain.admin.reservation.controller;

import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleUpdateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.MonthlyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.service.AdminReservationService;
import com.keunsori.keunsoriserver.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservation")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;
    private final ReservationService reservationService;

    // 기본 예약 관리 페이지 반환
    @GetMapping("/weekly-schedule")
    public ResponseEntity<List<WeeklyScheduleResponse>> findAllWeeklySchedules(){
        List<WeeklyScheduleResponse> responses = adminReservationService.findAllWeeklySchedules();
        return ResponseEntity.ok().body(responses);
    }

    // 주간 테이블 설정
    @PutMapping("/weekly-schedule")
    public ResponseEntity<Void> saveWeeklySchedule(@RequestBody List<WeeklyScheduleUpdateRequest> requests){
        adminReservationService.saveOrUpdateWeeklySchedule(requests);
        return ResponseEntity.ok().build();
    }

    // 일자별 관리 페이지 반환
    @GetMapping("/daily-schedule")
    public ResponseEntity<MonthlyScheduleResponse> findAllDailySchedulesAndResrvations(@RequestParam("month") String month) {
        MonthlyScheduleResponse response = reservationService.findMonthlySchedule(month);
        return ResponseEntity.ok().body(response);
    }

    // 일간 시간 설정
    @PostMapping("/daily-schedule")
    public ResponseEntity<Void> saveDailySchedule(@RequestBody DailyScheduleUpdateOrCreateRequest request){
        adminReservationService.saveOrUpdateDailySchedule(request);
        return ResponseEntity.ok().build();
    }

    // 관리자 예약 삭제
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservationByAdmin(@PathVariable(name = "reservationId") Long reservationId) {
        adminReservationService.deleteReservationByAdmin(reservationId);
        return ResponseEntity.noContent().build();
    }
}
