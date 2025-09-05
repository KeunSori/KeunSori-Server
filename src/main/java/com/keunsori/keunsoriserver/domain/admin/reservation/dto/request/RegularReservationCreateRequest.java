package com.keunsori.keunsoriserver.domain.admin.reservation.dto.request;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.global.annotation.ValidEnum;
import com.keunsori.keunsoriserver.global.util.DayOfWeekUtil;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record RegularReservationCreateRequest(
        @ValidEnum(enumClass = ReservationType.class, message = "[TEAM, LESSON] 중에 입력해주세요. (대소문자 구분 없음)")
        String reservationType,
        @ValidEnum(enumClass = Session.class, message = "[VOCAL, DRUM, GUITAR, BASS, KEYBOARD] 중에 입력해주세요. (대소문자 구분 없음)")
        String reservationSession,
        @ValidEnum(enumClass = DayOfWeek.class, message = "[MON, TUE, WED, THU, FRI, SAT, SUN] 중에 입력주세요. (대소문자 구분 없음)")
        String dayOfWeek,
        @Schema(example = "사무라이 하트", type = "string")
        String regularReservationTeamName,
        @Schema(example = "15:00", type = "string")
        LocalTime regularReservationStartTime,
        @Schema(example = "16:00", type = "string")
        LocalTime regularReservationEndTime,
        @Schema(example = "A123456", type = "string")
        String studentId,
        @Schema(example = "2025-07-31", type = "string")
        LocalDate applyStartDate,
        @Schema(example = "2025-08-31", type = "string")
        LocalDate applyEndDate
) {
    public RegularReservation toEntity(Member member){
            ReservationType reservationType = ReservationType.from(reservationType());
            DayOfWeek dayOfWeek = DayOfWeekUtil.fromString(dayOfWeek());
            return RegularReservation.builder()
                    .reservationType(reservationType)
                    .session(reservationType == ReservationType.TEAM ? Session.ALL : Session.from(reservationSession))
                    .dayOfWeek(dayOfWeek)
                    .regularReservationTeamName(regularReservationTeamName)
                    .startTime(regularReservationStartTime)
                    .endTime(regularReservationEndTime)
                    .applyStartDate(applyStartDate)
                    .applyEndDate(applyEndDate)
                    .member(member)
                    .build();
    }
}
