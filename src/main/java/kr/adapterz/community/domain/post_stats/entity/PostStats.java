package kr.adapterz.community.domain.post_stats.entity;

import jakarta.persistence.*;
import kr.adapterz.community.common.BaseEntity;
import lombok.*;

@Entity
@Table(name = "post_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PostStats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Long commentCount = 0L;

    public static PostStats createPostStats() {
        return PostStats.builder()
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .build();
    }

    public void increaseLikeCount() {
        this.likeCount = this.likeCount + 1;
    }

    public void decreaseLikeCount() {
        if (this.likeCount == 0L) {
            return;
        }
        this.likeCount = this.likeCount - 1;
    }

    public void synchronizeLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void increaseViewCount() {
        this.viewCount = this.viewCount + 1;
    }

}
