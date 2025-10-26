package kr.adapterz.community.domain.post_like.controller;

import kr.adapterz.community.auth.service.CustomUserDetails;
import kr.adapterz.community.domain.post_like.dto.PostLikeResponse;
import kr.adapterz.community.domain.post_like.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping
    public ResponseEntity<PostLikeResponse> like(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer memberId = userDetails.getMemberId();
        PostLikeResponse response = postLikeService.like(postId, memberId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping
    public ResponseEntity<PostLikeResponse> unlike(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer memberId = userDetails.getMemberId();
        PostLikeResponse response = postLikeService.unlike(postId, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
