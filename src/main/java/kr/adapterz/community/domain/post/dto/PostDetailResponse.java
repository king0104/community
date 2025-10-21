package kr.adapterz.community.domain.post.dto;

import kr.adapterz.community.domain.post.entity.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostDetailResponse {

    private Integer id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String memberNickname;
    private String memberProfileImageUrl;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;

    public static PostDetailResponse of(Post post) {
        List<String> imageUrls = post.getImages().stream()
                .map(image -> image.getS3Url())
                .toList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                imageUrls,
                post.getMember().getNickname(),
                post.getMember().getImage().getS3Url(),
                post.getPostStats().getViewCount(),
                post.getPostStats().getLikeCount(),
                post.getPostStats().getCommentCount(),
                post.getCreatedAt()
        );
    }

}
