package kr.adapterz.community.domain.post_like.controller;

import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.domain.post_like.dto.PostLikeResponse;
import kr.adapterz.community.domain.post_like.service.PostLikeService;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<PostLikeResponse> like(
            @PathVariable Integer postId
    ) {
        Integer memberId = getMemberId();
        PostLikeResponse response = postLikeService.like(postId, memberId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping
    public ResponseEntity<PostLikeResponse> unlike(
            @PathVariable Integer postId
    ) {
        Integer memberId = getMemberId();
        PostLikeResponse response = postLikeService.unlike(postId, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private Integer getMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return member.getId();
    }
}
