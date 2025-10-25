package kr.adapterz.community.domain.comment.repository;

import kr.adapterz.community.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member m " +
           "JOIN FETCH m.image " +
           "WHERE c.post.id = :postId AND c.isDeleted = false " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdWithMember(@Param("postId") Integer postId);

}
