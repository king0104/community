package kr.adapterz.community.domain.post.dto;

import kr.adapterz.community.domain.post.entity.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreateResponse {

    private Integer id;
    private String title;
    private String content;
    private Integer memberId;
    private String memberNickname;
    private LocalDateTime createdAt;

    public static PostCreateResponse of(Post post) {
        return new PostCreateResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getId(),
                post.getMember().getNickname(),
                post.getCreatedAt()
        );
    }

}