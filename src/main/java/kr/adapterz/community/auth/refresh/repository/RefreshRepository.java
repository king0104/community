package kr.adapterz.community.auth.refresh.repository;


import jakarta.transaction.Transactional;
import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

}
