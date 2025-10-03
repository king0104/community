package kr.adapterz.community.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.adapterz.community.auth.RoleType;
import kr.adapterz.community.auth.SocialProviderType;
import kr.adapterz.community.common.BaseTimeEntity;
import kr.adapterz.community.member.dto.MemberPatchRequest;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, updatable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_lock", nullable = false)
    private Boolean isLock;

    @Column(name = "is_social", nullable = false)
    private Boolean isSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider_type")
    private SocialProviderType socialProviderType;

    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profile_image_url", nullable = false)
    private String profile_image_url;

    protected Member() {

    }

    private Member(
            String email,
            String password,
            String nickname,
            String profile_image_url
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile_image_url = profile_image_url;
    }

    private Member(String email, String password, Boolean isLock, Boolean isSocial,
                  SocialProviderType socialProviderType, RoleType roleType, String nickname, String profile_image_url) {
        this.email = email;
        this.password = password;
        this.isLock = isLock;
        this.isSocial = isSocial;
        this.socialProviderType = socialProviderType;
        this.roleType = roleType;
        this.nickname = nickname;
        this.profile_image_url = profile_image_url;
    }

    public static Member createMember(
            String email,
            String password,
            String nickname,
            String profile_image_url
    ) {
        return new Member(
                email,
                password,
                nickname,
                profile_image_url
        );
    }

    public static Member createSocialMember(
            String email,
            String nickname,
            SocialProviderType socialProviderType,
            String profile_image_url
    ) {
        return new Member(
                email,
                "",
                false,
                true,
                socialProviderType,
                RoleType.MEMBER,
                nickname,
                profile_image_url
        );
    }

    public void updateMember(MemberPatchRequest request) {
        this.nickname = request.getNickname();
        this.password = request.getPassword();
    }
}
