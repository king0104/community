package kr.adapterz.community.domain.member.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.auth.annotation.LoginMember;
import kr.adapterz.community.domain.member.dto.JoinRequest;
import kr.adapterz.community.domain.member.dto.MemberGetResponse;
import kr.adapterz.community.domain.member.dto.MemberPatchRequest;
import kr.adapterz.community.domain.member.dto.MemberPatchResponse;
import kr.adapterz.community.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 (AuthController로 이동 권장)
     */
    @PostMapping
    public ResponseEntity<Void> join(@Valid @RequestBody JoinRequest joinRequest) {
        memberService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<MemberGetResponse> getMember(
            @LoginMember Integer memberId
    ) {
        MemberGetResponse response = memberService.getMemberById(memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 수정
     */
    @PatchMapping("/me")
    public ResponseEntity<MemberPatchResponse> patchMember(
            @LoginMember Integer memberId,
            @Valid @RequestBody MemberPatchRequest request
    ) {
        MemberPatchResponse response = memberService.patchMember(memberId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(
            @LoginMember Integer memberId
    ) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }
}