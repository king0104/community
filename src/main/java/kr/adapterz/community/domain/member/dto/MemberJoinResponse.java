package kr.adapterz.community.domain.member.dto;

import kr.adapterz.community.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MemberJoinResponse {
    private Integer memberId;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public static MemberJoinResponse from(Member member) {
        return MemberJoinResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getImage().getS3Url())
                .build();
    }
}
