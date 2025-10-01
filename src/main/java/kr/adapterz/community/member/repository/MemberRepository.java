package kr.adapterz.community.member.repository;

import java.util.Optional;
import kr.adapterz.community.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    Member findByEmail(String email);

    Optional<Member> findByEmailAndIsLockAndIsSocial(String email, Boolean isLock, Boolean isSocial);

}
