package kr.adapterz.community.domain.comment.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentListPageResponse {

    private List<CommentResponse> comments;
    private Integer nextCursor;
    private Boolean hasNext;

    public static CommentListPageResponse of(List<CommentResponse> comments, Integer nextCursor, Boolean hasNext) {
        return new CommentListPageResponse(comments, nextCursor, hasNext);
    }

}
