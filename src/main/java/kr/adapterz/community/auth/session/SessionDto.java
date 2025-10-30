package kr.adapterz.community.auth.session;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionDto implements Serializable {

    private Integer memberId;
    private String role;

    public static SessionDto of(Integer memberId, String role) {
        return SessionDto.builder()
                .memberId(memberId)
                .role(role)
                .build();
    }
}