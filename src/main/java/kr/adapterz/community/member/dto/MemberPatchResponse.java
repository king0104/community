package kr.adapterz.community.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MemberPatchResponse {
    private Long id;

    private MemberPatchResponse(
            Long id
    ) {
        this.id = id;
    }

    public static MemberPatchResponse createMemberPatchResponse(
            Long id
    ) {
        return new MemberPatchResponse(id);
    }
}
