package kr.adapterz.community.domain.comment.repository;

import kr.adapterz.community.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member m " +
           "JOIN FETCH m.image " +
           "WHERE c.post.id = :postId " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdWithMember(@Param("postId") Integer postId);

    @Query(value = "SELECT c.* FROM comment c " +
                   "JOIN member m ON c.member_id = m.id " +
                   "WHERE c.post_id = :postId " +
                   "AND c.is_deleted = false " +
                   "AND (:cursor IS NULL OR c.id > :cursor) " +
                   "ORDER BY c.id ASC " +
                   "LIMIT :limit",
           nativeQuery = true)
    List<Comment> findCommentsWithCursor(@Param("postId") Integer postId,
                                         @Param("cursor") Integer cursor,
                                         @Param("limit") int limit);

    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member m " +
           "JOIN FETCH m.image " +
           "WHERE c.id = :commentId AND c.isDeleted = false")
    Optional<Comment> findByIdWithMember(@Param("commentId") Integer commentId);

}
