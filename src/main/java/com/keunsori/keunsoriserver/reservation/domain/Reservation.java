package com.keunsori.keunsoriserver.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.keunsori.keunsoriserver.member.Member;
import com.keunsori.keunsoriserver.reservation.domain.vo.ReservationType;
import com.keunsori.keunsoriserver.reservation.domain.vo.Session;

import java.sql.Time;
import java.util.Date;
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
    private Date date;

    @Column(name = "reservation_start_time")
    private Time startTime;

    @Column(name = "reservation_end_time")
    private Time endTime;

    @Column(name = "reservation_type")
    private ReservationType type;

    @Column(name = "reservation_session")
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private Reservation(Date date, Time startTime, Time endTime, ReservationType type,
            Session session,
            Member member) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.session = session;
        this.member = member;
    }

    /**
     * Entity Method
     **/

    public void updateReservation(ReservationType type, Session session, Date reservationDate,
            Time reservationStartTime, Time reservationEndTime) {
        this.type = type;
        this.session = session;
        this.date = reservationDate;
        this.startTime = reservationStartTime;
        this.endTime = reservationEndTime;
    }
}
