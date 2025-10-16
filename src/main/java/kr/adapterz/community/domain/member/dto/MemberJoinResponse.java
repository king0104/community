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

    // image까지 한 번에 로딩한 member 가져와야한다
    // image 있으면 url 주기 / 없으면 null
    public static MemberJoinResponse from(Member member
    ) {
        return MemberJoinResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getImage() != null ? member.getImage().getS3Url() : null)
                .build();
    }
}
