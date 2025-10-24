package kr.adapterz.community.domain.comment.dto;

import kr.adapterz.community.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateResponse {

    private Integer id;
    private Integer postId;
    private Integer memberId;
    private String memberNickname;
    private Integer parentId;
    private String content;
    private LocalDateTime createdAt;

    public static CommentCreateResponse of(Comment comment) {
        return CommentCreateResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .memberId(comment.getMember().getId())
                .memberNickname(comment.getMember().getNickname())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
