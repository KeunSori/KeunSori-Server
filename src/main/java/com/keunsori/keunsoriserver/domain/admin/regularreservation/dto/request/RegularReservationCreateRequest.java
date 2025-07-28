package com.keunsori.keunsoriserver.domain.admin.regularreservation.dto.request;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.RegularReservation;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo.RegularReservationType;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.global.annotation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record RegularReservationCreateRequest(
        @ValidEnum(enumClass = RegularReservationType.class, message = "[TEAM, LESSON] 중에 입력해주세요. (대소문자 구분 없음)")
        String regularReservationType,
        @Schema(example = "MON", type = "string")
        DayOfWeek dayOfWeek,
        @Schema(example = "사무라이 하트", type = "string")
        String regularReservationTeamName,
        @Schema(example = "15:00", type = "string")
        LocalTime regularReservationStartTime,
        @Schema(example = "16:00", type = "string")
        LocalTime regularReservationEndTime,
        @Schema(example = "A123456", type = "string")
        String studentId
) {
    public RegularReservation toEntity(Member member){
            RegularReservationType regularReservationType = RegularReservationType.from(regularReservationType());
            return RegularReservation.builder()
                    .regularReservationType(regularReservationType)
                    .dayOfWeek(dayOfWeek)
                    .regularReservationTeamName(regularReservationTeamName)
                    .startTime(regularReservationStartTime)
                    .endTime(regularReservationEndTime)
                    .member(member)
                    .build();
    }
}
