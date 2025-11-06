package kr.adapterz.community.domain.post_like.controller;

import kr.adapterz.community.auth.annotation.LoginMember;
import kr.adapterz.community.domain.post_like.dto.PostLikeResponse;
import kr.adapterz.community.domain.post_like.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    /**
     * 좋아요 등록
     */
    @PostMapping
    public ResponseEntity<PostLikeResponse> like(
            @PathVariable Integer postId,
            @LoginMember Integer memberId
    ) {
        PostLikeResponse response = postLikeService.like(postId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping
    public ResponseEntity<PostLikeResponse> unlike(
            @PathVariable Integer postId,
            @LoginMember Integer memberId
    ) {
        PostLikeResponse response = postLikeService.unlike(postId, memberId);
        return ResponseEntity.ok(response);
    }
}