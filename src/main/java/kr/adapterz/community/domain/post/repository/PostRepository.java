package kr.adapterz.community.domain.post.repository;

import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.repository.custom.PostRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {
}