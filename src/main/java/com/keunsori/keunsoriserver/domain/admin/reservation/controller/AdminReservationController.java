package com.keunsori.keunsoriserver.domain.admin.reservation.controller;

import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.RegularReservationResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.DailyScheduleUpdateOrCreateRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.request.WeeklyScheduleManagementRequest;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.DailyAvailableResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleManagementResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.dto.response.WeeklyScheduleResponse;
import com.keunsori.keunsoriserver.domain.admin.reservation.service.AdminReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservation")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    // 기본 주간 예약 관리 페이지 반환
    @GetMapping("/weekly-schedule")
    public ResponseEntity<List<WeeklyScheduleResponse>> findAllWeeklySchedules(){
        List<WeeklyScheduleResponse> responses = adminReservationService.findAllWeeklySchedules();
        return ResponseEntity.ok().body(responses);
    }

    // 주간 스케줄 + 정기 예약 통합 저장, 수정, 삭제
    @PutMapping("/weekly-schedule/management")
    public ResponseEntity<WeeklyScheduleManagementResponse> saveWeeklyScheduleAndRegularReservations(@Valid @RequestBody WeeklyScheduleManagementRequest request){
        WeeklyScheduleManagementResponse result = adminReservationService.saveWeeklyScheduleAndRegularReservations(request, request.force());
        return ResponseEntity.ok().body(result);
    }

    // 일자별 관리 페이지 반환
    @GetMapping("/daily-schedule")
    public ResponseEntity<List<DailyAvailableResponse>> findAllDailySchedulesAndReservations(@RequestParam("month") String month) {
        List<DailyAvailableResponse> responses = adminReservationService.findDailyAvailableByMonth(month);
        return ResponseEntity.ok().body(responses);
    }

    // 일간 시간 설정
    @PutMapping("/daily-schedule")
    public ResponseEntity<Void> saveDailySchedule(@RequestBody DailyScheduleUpdateOrCreateRequest request){
        adminReservationService.saveDailySchedule(request);
        return ResponseEntity.ok().build();
    }

    // 관리자 예약 단건 삭제
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservationByAdmin(@PathVariable(name = "reservationId") Long reservationId) {
        adminReservationService.deleteReservationByAdmin(reservationId);
        return ResponseEntity.noContent().build();
    }

    // 관리자 예약 다중 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteReservationsByAdmin(@RequestBody List<Long> reservationIds) {
        adminReservationService.deleteReservationsByAdmin(reservationIds);
        return ResponseEntity.noContent().build();
    }

    // 정기 예약 전체 조회(화살표 눌렀을 때 해당 요일 밑에 전체 조회 용)
    @GetMapping("/weekly-schedule/by-day")
    public ResponseEntity<List<RegularReservationResponse>> findRegularReservationsByDay(@RequestParam("dayOfWeek") DayOfWeek dayOfWeek){
        List<RegularReservationResponse> responses = adminReservationService.findRegularReservationsByDay(dayOfWeek);
        return ResponseEntity.ok().body(responses);
    }
}
