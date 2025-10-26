package kr.adapterz.community.domain.post_like.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeResponse {

    private Boolean isLiked;
    private Long likeCount;

    public static PostLikeResponse of(Boolean isLiked, Long likeCount) {
        return PostLikeResponse.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}
