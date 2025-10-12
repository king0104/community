package kr.adapterz.community.domain.member.dto;

import kr.adapterz.community.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MemberPatchResponse {

    private Integer id;

    public static MemberPatchResponse from(Member member) {
        return MemberPatchResponse.builder()
                .id(member.getId())
                .build();
    }
}