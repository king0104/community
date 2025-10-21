package kr.adapterz.community.domain.post.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @Size(max = 26, message = "제목은 26자 이내로 작성해주세요.")
    private String title;

    private String content;

    private List<Integer> imageIds;

}