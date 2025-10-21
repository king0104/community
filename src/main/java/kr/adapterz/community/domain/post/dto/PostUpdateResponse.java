package kr.adapterz.community.domain.post.dto;

import kr.adapterz.community.domain.post.entity.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostUpdateResponse {

    private Integer id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime updatedAt;

    public static PostUpdateResponse of(Post post) {
        List<String> imageUrls = post.getImages().stream()
                .map(image -> image.getS3Url())
                .toList();

        return PostUpdateResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrls(imageUrls)
                .updatedAt(post.getUpdatedAt())
                .build();
    }

}