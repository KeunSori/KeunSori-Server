package com.keunsori.keunsoriserver.domain.member.repository;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStudentId(String studentId);
    Optional<Member> findByStudentIdIgnoreCase(String studentId);
    boolean existsByStudentId(String studentId);
    boolean existsByEmail(String email);

    List<Member> findAllByStatus(MemberStatus status);
}
