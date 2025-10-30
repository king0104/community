package kr.adapterz.community.auth.dto;

import kr.adapterz.community.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class LoginResponse {

    private Integer memberId;
    private String email;
    private String nickname;
    private String role;

    public static LoginResponse from(Member member) {
        return LoginResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }
}