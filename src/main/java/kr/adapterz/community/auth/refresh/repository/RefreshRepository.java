package kr.adapterz.community.auth.refresh.repository;

import java.util.Optional;
import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    Optional<RefreshEntity> findByRefresh(String refresh);

    void deleteByRefresh(String refresh);

    void deleteByEmail(String email);
}