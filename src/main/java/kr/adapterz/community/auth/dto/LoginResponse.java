package kr.adapterz.community.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
    private String role;
}