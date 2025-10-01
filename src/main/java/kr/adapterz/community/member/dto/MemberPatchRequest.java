package kr.adapterz.community.member.dto;

import lombok.Getter;

@Getter
public class MemberPatchRequest {
    private String email;
    private String nickname;
    private String password;
    private String profileImageUrl;
}
