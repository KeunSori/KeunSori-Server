package com.keunsori.keunsoriserver.domain.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(name = "reservation_date")
    private LocalDate date;

    @Column(name = "reservation_start_time")
    private LocalTime startTime;

    @Column(name = "reservation_end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_type")
    private ReservationType reservationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_session")
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Reservation(LocalDate date, LocalTime startTime, LocalTime endTime, ReservationType reservationType,
            Session session,
            Member member) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationType = reservationType;
        this.session = session;
        this.member = member;
    }

    /**
     * Entity Method
     **/

    public void updateReservation(ReservationType reservationType, Session session, LocalDate reservationDate,
            LocalTime reservationStartTime, LocalTime reservationEndTime) {
        this.reservationType = reservationType;
        this.session = session;
        this.date = reservationDate;
        this.startTime = reservationStartTime;
        this.endTime = reservationEndTime;
    }

    public boolean hasMember(Member checkMember) {
        return member.equals(checkMember);
    }

    public boolean isComplete() {
        // TODO : new Date 호출 시점을 컨트롤러 호출 시점과 맞추기
        // TODO : LocalDate 테스트가 가능하도록 분리하기
        return LocalDate.now().isAfter(date);
    }

    public Long getMemberId() {
        if (member == null) {
            return null;
        }
        return member.getId();
    }

    public String getMemberName() {
        if (member == null) {
            return "(알 수 없음)";
        }
        return member.getName();
    }
}
