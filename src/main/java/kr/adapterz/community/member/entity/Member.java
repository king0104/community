package kr.adapterz.community.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.adapterz.community.common.BaseTimeEntity;
import lombok.Getter;

@Entity
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profile_image_url", nullable = false)
    private String profile_image_url;

    @Column(name = "role", nullable = false)
    private String role;

    protected Member () {

    }

    private Member(
            String email,
            String password,
            String nickname,
            String profile_image_url,
            String role
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile_image_url = profile_image_url;
        this.role = role;
    }

    public static Member createMember(
            String email,
            String password,
            String nickname,
            String profile_image_url,
            String role
    ) {
        return new Member(
                email,
                password,
                nickname,
                profile_image_url,
                role
        );
    }
}
