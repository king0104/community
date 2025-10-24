package kr.adapterz.community.domain.comment.entity;

import jakarta.persistence.*;
import kr.adapterz.community.common.BaseEntity;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.post.entity.Post;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public static Comment createComment(Post post, Member member, String content) {
        return Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .build();
    }

    public static Comment createComment(Post post, Member member, Comment parent, String content) {
        return Comment.builder()
                .post(post)
                .member(member)
                .parent(parent)
                .content(content)
                .build();
    }

}