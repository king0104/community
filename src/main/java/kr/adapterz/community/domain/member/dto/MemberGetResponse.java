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
    private Integer profileImageId;

    public static MemberGetResponse of(
            Integer memberId,
            String email,
            String nickname,
            Integer profileImageId
    ) {
        return MemberGetResponse.builder()
                .memberId(memberId)
                .email(email)
                .nickname(nickname)
                .profileImageId(profileImageId)
                .build();
    }
}
