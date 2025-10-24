package kr.adapterz.community.domain.comment.repository;

import kr.adapterz.community.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
