package com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo.RegularReservationSession;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo.RegularReservationType;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.global.annotation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record RegularReservationCreateRequest(
        @ValidEnum(enumClass = RegularReservationType.class, message = "[TEAM, LESSON] 중에 입력해주세요. (대소문자 구분 없음)")
        String regularReservationType,
        @ValidEnum(enumClass = RegularReservationSession.class, message = "[VOCAL, DRUM, GUITAR, BASS, KEYBOARD] 중에 입력해주세요. (대소문자 구분 없음)")
        String regularReservationSession,
        @Schema(example = "MON", type = "string")
        DayOfWeek dayOfWeek,
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
            RegularReservationType regularReservationType = RegularReservationType.from(regularReservationType());
            return RegularReservation.builder()
                    .regularReservationType(regularReservationType)
                    .regularReservationSession(regularReservationType.equals(RegularReservationType.TEAM) ? RegularReservationSession.ALL : RegularReservationSession.from(regularReservationSession))
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
