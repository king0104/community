package kr.adapterz.community.member.controller;

import kr.adapterz.community.member.dto.JoinRequest;
import kr.adapterz.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {
        memberService.join(joinRequest);

        return "ok";
    }

    // 자체 로그인 유저 존재 확인

    // 회원가입

    // 유저 정보

    // 유저 수정 (자체 로그인 유저만)

    // 유저 제거 (자체/소셜)


}
