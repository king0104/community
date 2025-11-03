package kr.adapterz.community.auth.refresh.entity;

import jakarta.persistence.*;
import kr.adapterz.community.common.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class RefreshEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiration;

    public static RefreshEntity createRefreshToken(String email, String refreshToken, LocalDateTime expiration) {
        return RefreshEntity.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiration(expiration)
                .build();
    }

    public void updateRefreshToken(String refreshToken, LocalDateTime expiration) {
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiration);
    }
}
