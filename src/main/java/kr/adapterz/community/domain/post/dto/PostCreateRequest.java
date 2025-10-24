package kr.adapterz.community.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 26, message = "제목은 26자 이내로 작성해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private List<Integer> imageIds;

}