package com.keunsori.keunsoriserver.domain.member.repository;

import com.keunsori.keunsoriserver.domain.member.domain.Member;
import com.keunsori.keunsoriserver.domain.member.domain.vo.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByStudentId(String studentId);
    Optional<Member> findByStudentIdIgnoreCase(String studentId);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(m) > 0 "
           + "FROM Member m "
          + "WHERE UPPER(m.studentId) = UPPER(:studentId) ")
    boolean existsByStudentId(@Param("studentId") String studentId);

    List<Member> findAllByStatus(MemberStatus status);
}
