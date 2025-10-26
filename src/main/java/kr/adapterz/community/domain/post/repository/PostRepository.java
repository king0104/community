package kr.adapterz.community.domain.post.repository;

import java.util.Optional;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.repository.custom.PostRepositoryCustom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p JOIN FETCH p.postStats WHERE p.id = :postId")
    Optional<Post> findByIdWithStatsForUpdate(@Param("postId") Integer postId);
}
