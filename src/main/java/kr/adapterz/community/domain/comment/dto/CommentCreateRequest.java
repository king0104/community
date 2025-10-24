package kr.adapterz.community.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Integer postId;

    private Integer parentId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

}
