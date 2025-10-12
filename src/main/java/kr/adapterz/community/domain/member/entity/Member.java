package kr.adapterz.community.domain.member.entity;

import jakarta.persistence.*;
import kr.adapterz.community.common.BaseEntity;
import kr.adapterz.community.domain.comment.entity.Comment;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post_like.entity.PostLike;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 512)
    private String profileImgUrl;

    @Column
    private String role;

    public static Member createMember(
            String email,
            String password,
            String nickname,
            String profileImgUrl,
            String role
    ) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .role(role)
                .build();
    }

    public void updateProfile(String nickname, String profileImgUrl) {
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
    }

    public void withdraw() {
        this.delete();
    }

    public boolean isWithdrawn() {
        return this.getIsDeleted();
    }

}