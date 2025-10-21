package kr.adapterz.community.domain.post.dto;

import kr.adapterz.community.domain.post.entity.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostListResponse {

    private Integer id;
    private String title;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private String memberNickname;
    private String memberProfileImageUrl;

    public static PostListResponse of(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getPostStats().getViewCount(),
                post.getPostStats().getLikeCount(),
                post.getPostStats().getCommentCount(),
                post.getCreatedAt(),
                post.getMember().getNickname(),
                post.getMember().getImage().getS3Url()
        );
    }

}