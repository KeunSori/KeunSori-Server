package com.keunsori.keunsoriserver.domain.admin.regularreservation.domain;

import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo.RegularReservationSession;
import com.keunsori.keunsoriserver.domain.admin.regularreservation.domain.vo.RegularReservationType;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.global.exception.RegularReservationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.REGULAR_RESERVATION_NOT_DELETABLE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegularReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regular_reservation_id")
    private Long id;

    @Column(name = "regular_reservation_day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "regular_reservation_start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "regular_reservation_end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "regular_reservation_type")
    private RegularReservationType regularReservationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "regular_reservation_session", nullable = false)
    private RegularReservationSession regularReservationSession;

    @Column(name = "regular_reservation_team_name", nullable = false)
    private String regularReservationTeamName;

    @Column(name = "apply_start_date", nullable = false)
    private LocalDate applyStartDate;

    @Column(name = "apply_end_date", nullable = false)
    private LocalDate applyEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private RegularReservation(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, RegularReservationType regularReservationType,
                               RegularReservationSession regularReservationSession, String regularReservationTeamName,
                               LocalDate applyStartDate, LocalDate applyEndDate,
                               Member member) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.regularReservationType = regularReservationType;
        this.regularReservationSession = regularReservationSession;
        this.regularReservationTeamName = regularReservationTeamName;
        this.applyStartDate = applyStartDate;
        this.applyEndDate = applyEndDate;
        this.member = member;
    }

    // 예약한 멤버 일치 여부
    public void validateReservedBy(Member loginMember) {
        if (!this.member.equals(loginMember)) {
            throw new RegularReservationException(REGULAR_RESERVATION_NOT_DELETABLE);
        }
    }

    // 팀장 member_id 반환
    public Long getMemberId() {
        return member.getId();
    }

    // 팀장 학번 반환
    public String getStudentId(){
        return member != null ? member.getStudentId() : "(알 수 없음)";
    }
    
    // 특정 시간 범위 밖에 있는지 확인
    public boolean isOutOfScheduleTime(LocalTime scheduleStartTime, LocalTime scheduleEndTime) {
        return this.startTime.isBefore(scheduleStartTime) || this.endTime.isAfter(scheduleEndTime);
    }
}
