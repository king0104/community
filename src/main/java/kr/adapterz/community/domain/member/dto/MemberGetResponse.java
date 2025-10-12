package kr.adapterz.community.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MemberGetResponse {
    private Integer memberId;
    private String email;
    private String nickname;
    private String profileImgUrl;

    public static MemberGetResponse of(
            Integer memberId,
            String password,
            String nickname,
            String profileImgUrl
    ) {
        return MemberGetResponse.builder()
                .memberId(memberId)
                .email(password)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .build();
    }
}
