package kr.adapterz.community.domain.post.repository.custom;

import kr.adapterz.community.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {

    List<Post> findPostsWithCursor(Integer cursor, int size);

    Optional<Post> findPostDetailById(Integer postId);

}