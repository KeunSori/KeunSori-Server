package com.keunsori.keunsoriserver.member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
    Optional<Member> findByHongikgmail(String hongikgmail);
    boolean existsByHongikgmail(String hongikgmail);
}
