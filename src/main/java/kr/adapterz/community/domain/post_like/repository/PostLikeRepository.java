package kr.adapterz.community.domain.post_like.repository;

import java.util.Optional;
import kr.adapterz.community.domain.post_like.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
    Optional<PostLike> findByPostIdAndMemberId(
            @Param("postId") Integer postId,
            @Param("memberId") Integer memberId
    );

    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN TRUE ELSE FALSE END FROM PostLike pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
    boolean existsByPostIdAndMemberId(
            @Param("postId") Integer postId,
            @Param("memberId") Integer memberId
    );

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    long countByPostId(@Param("postId") Integer postId);
}

