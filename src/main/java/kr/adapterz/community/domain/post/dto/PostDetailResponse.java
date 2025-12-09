package kr.adapterz.community.domain.post.dto;

import kr.adapterz.community.domain.post.entity.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PostDetailResponse {

    private Integer id;
    private String title;
    private String content;
    private List<Integer> imageIds;
    private List<String> imageUrls;
    private Integer memberId;
    private String memberNickname;
    private String memberProfileImageUrl;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLikedByMe;
    private LocalDateTime createdAt;

    public static PostDetailResponse of(Post post, Boolean isLikedByMe) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageIds(extractImageIds(post))
                .imageUrls(extractImageUrls(post))
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .memberProfileImageUrl(post.getMember().getImage().getS3Url())
                .viewCount(post.getPostStats().getViewCount())
                .likeCount(post.getPostStats().getLikeCount())
                .commentCount(post.getPostStats().getCommentCount())
                .isLikedByMe(isLikedByMe)
                .createdAt(post.getCreatedAt())
                .build();
    }

    private static List<Integer> extractImageIds(Post post) {
        return post.getImages().stream()
                .map(image -> image.getId())
                .toList();
    }

    private static List<String> extractImageUrls(Post post) {
        return post.getImages().stream()
                .map(image -> image.getS3Url())
                .toList();
    }
}