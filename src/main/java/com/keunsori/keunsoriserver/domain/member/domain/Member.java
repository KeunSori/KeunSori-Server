package com.keunsori.keunsoriserver.domain.member.domain;

import com.keunsori.keunsoriserver.domain.common.BaseEntity;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import com.keunsori.keunsoriserver.global.exception.MemberException;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INVALID_STATUS_FOR_APPROVAL;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_DOES_NOT_MATCH_WITH_EMAIL;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 7, unique = true, nullable = false)
    private String studentId;

    @Column(length = 50)
    private String email;

    @Column(length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberStatus status;

    @Column(length = 20)
    private String name;

    private LocalDateTime approvalDate;

    @Builder
    public Member(String studentId, String email, String password, String name, MemberStatus status) {
        this.studentId = studentId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = status;
    }

    public void approve() {
        if (this.status != MemberStatus.승인대기) {
            throw new MemberException(INVALID_STATUS_FOR_APPROVAL);
        }
        this.status = MemberStatus.일반;
        this.approvalDate = LocalDateTime.now();
    }

    /**
     * 데이터 검증 로직
     */
    public void validateEmail(String email) {
        if (!this.email.equals(email)) {
            throw new MemberException(STUDENT_ID_DOES_NOT_MATCH_WITH_EMAIL);
        }
    }

    /**
     * 데이터 조회 로직
     */

    public boolean isAdmin() {
        return status.equals(MemberStatus.관리자);
    }

    public boolean hasEmail(String email) {
        return this.email.equals(email);
    }

    /**
     * 데이터 변경 로직
     */

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    @PrePersist
    public void convertStudentIdToUpperCase() {
        this.studentId = studentId.toUpperCase();
    }
}
