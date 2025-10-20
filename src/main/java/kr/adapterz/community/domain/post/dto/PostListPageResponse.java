package kr.adapterz.community.domain.post.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostListPageResponse {

    private List<PostListResponse> posts;
    private Integer nextCursor;
    private Boolean hasNext;

    public static PostListPageResponse of(List<PostListResponse> posts, Integer nextCursor, Boolean hasNext) {
        return new PostListPageResponse(posts, nextCursor, hasNext);
    }

}