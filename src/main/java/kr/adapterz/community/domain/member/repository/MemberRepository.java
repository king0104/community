package kr.adapterz.community.domain.member.repository;

import java.util.Optional;
import kr.adapterz.community.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
