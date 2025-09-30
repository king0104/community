package kr.adapterz.community.member.repository;

import kr.adapterz.community.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    Member findByEmail(String email);

}
