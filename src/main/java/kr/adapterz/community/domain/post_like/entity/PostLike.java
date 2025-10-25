package kr.adapterz.community.domain.post_like.entity;

import jakarta.persistence.*;
import kr.adapterz.community.common.BaseEntity;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.post.entity.Post;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_like_post_member", columnNames = {"post_id", "member_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static PostLike createPostLike(Post post, Member member) {
        return PostLike.builder()
                .post(post)
                .member(member)
                .build();
    }

}
