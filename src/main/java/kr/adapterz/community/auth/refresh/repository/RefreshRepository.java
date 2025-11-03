package kr.adapterz.community.auth.refresh.repository;

import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Optional<RefreshEntity> findByEmail(String email);

    Optional<RefreshEntity> findByRefreshToken(String refreshToken);

    void deleteByEmail(String email);

    boolean existsByRefreshToken(String refreshToken);
}