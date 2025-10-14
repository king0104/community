package kr.adapterz.community.auth.refresh.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ReissueTokenDto {
    private String accessToken;
    private String refreshToken;

    public static ReissueTokenDto of(String accessToken, String refreshToken) {
        return ReissueTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
