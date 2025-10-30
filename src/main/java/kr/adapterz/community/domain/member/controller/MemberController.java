package kr.adapterz.community.domain.member.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.domain.member.dto.JoinRequest;
import kr.adapterz.community.domain.member.dto.MemberGetResponse;
import kr.adapterz.community.domain.member.dto.MemberPatchRequest;
import kr.adapterz.community.domain.member.dto.MemberPatchResponse;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> join(@Valid @RequestBody JoinRequest joinRequest) {
        memberService.join(joinRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberGetResponse> getMember(
    ) {
        Member member = memberService.findByEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MemberGetResponse.of(
                        member.getId(),
                        member.getEmail(),
                        member.getNickname(),
                        member.getImage().getId()
                ));
    }

    @PatchMapping("/me")
    public ResponseEntity<MemberPatchResponse> patchMember(
            @RequestBody @Valid MemberPatchRequest request
    ) {
        MemberPatchResponse response = memberService.patchMember(email, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(
    ) {

        memberService.deleteMember(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
