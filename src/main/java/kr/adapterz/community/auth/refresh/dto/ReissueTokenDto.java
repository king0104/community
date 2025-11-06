package kr.adapterz.community.auth.refresh.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueTokenDto {
    private String accessToken;
    private String refreshToken;
}
