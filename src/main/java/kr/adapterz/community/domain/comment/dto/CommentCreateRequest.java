package kr.adapterz.community.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    private Integer parentId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

}
