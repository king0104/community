package kr.adapterz.community.domain.member.repository;

import kr.adapterz.community.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    Member findByEmail(String email);

}
