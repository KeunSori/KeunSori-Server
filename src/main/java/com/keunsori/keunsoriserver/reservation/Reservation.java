package com.keunsori.keunsoriserver.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import com.keunsori.keunsoriserver.reservation.vo.ReservationType;
import com.keunsori.keunsoriserver.reservation.vo.Session;

import java.sql.Time;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue
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
}
