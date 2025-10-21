package kr.adapterz.community.domain.post.entity;
import jakarta.persistence.*;
import kr.adapterz.community.common.BaseEntity;
import kr.adapterz.community.domain.image.entity.Image;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.post_stats.entity.PostStats;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    // Post와 PostStats는 생명주기 완전 일치
    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "post_stats_id", nullable = false, unique = true)
    private PostStats postStats;

    public static Post createPost(Member member, String title, String content, PostStats postStats) {
        return Post.builder()
                .member(member)
                .title(title)
                .content(content)
                .postStats(postStats)
                .build();
    }

    public void updatePost(String title, String content, List<Image> images) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
    }

}