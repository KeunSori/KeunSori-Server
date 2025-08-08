package com.keunsori.keunsoriserver.domain.admin.reservation.domain;

import com.keunsori.keunsoriserver.domain.admin.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.admin.reservation.domain.vo.Session;
import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.global.exception.MemberException;
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
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_INVALID_FORMAT;

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
    private ReservationType reservationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "regular_reservation_session", nullable = false)
    private Session session;

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
    private RegularReservation(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, ReservationType reservationType,
                               Session session, String regularReservationTeamName,
                               LocalDate applyStartDate, LocalDate applyEndDate,
                               Member member) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationType = reservationType;
        this.session = session;
        this.regularReservationTeamName = regularReservationTeamName;
        this.applyStartDate = applyStartDate;
        this.applyEndDate = applyEndDate;
        this.member = member;
    }

    // 예약한 멤버 일치 여부(관리자 검증)
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
    public String getStudentId() {
        if (member.getStudentId() == null) {
            throw new MemberException(STUDENT_ID_INVALID_FORMAT);
        }
        return member.getStudentId();
    }
    
    // 특정 시간 범위 밖에 있는지 확인
    public boolean isOutOfScheduleTime(LocalTime scheduleStartTime, LocalTime scheduleEndTime) {
        return this.startTime.isBefore(scheduleStartTime) || this.endTime.isAfter(scheduleEndTime);
    }
}
