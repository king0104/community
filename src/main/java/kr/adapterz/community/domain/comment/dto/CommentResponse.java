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
public class CommentResponse {

    private Integer id;
    private String content;
    private Integer memberId;
    private String memberNickname;
    private String memberProfileImageUrl;
    private Integer parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .memberId(comment.getMember().getId())
                .memberNickname(comment.getMember().getNickname())
                .memberProfileImageUrl(comment.getMember().getImage() != null ?
                        comment.getMember().getImage().getS3Url() : null)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

}