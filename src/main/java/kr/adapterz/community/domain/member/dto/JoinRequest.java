package kr.adapterz.community.domain.member.dto;

import lombok.Getter;

@Getter
public class JoinRequest {
    private String email;
    private String password;
    private String nickname;
    private String profile_image_url;
    private String role;
}
