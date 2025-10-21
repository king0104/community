package kr.adapterz.community.domain.post.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.auth.service.CustomUserDetails;
import kr.adapterz.community.domain.post.dto.PostCreateRequest;
import kr.adapterz.community.domain.post.dto.PostCreateResponse;
import kr.adapterz.community.domain.post.dto.PostDetailResponse;
import kr.adapterz.community.domain.post.dto.PostListPageResponse;
import kr.adapterz.community.domain.post.dto.PostUpdateRequest;
import kr.adapterz.community.domain.post.dto.PostUpdateResponse;
import kr.adapterz.community.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest request
    ) {
        Integer memberId = userDetails.getMemberId();
        PostCreateResponse response = postService.createPost(memberId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<PostListPageResponse> getPostList(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        PostListPageResponse response = postService.getPostList(cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Integer postId) {
        PostDetailResponse response = postService.getPostDetail(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostUpdateResponse> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        Integer memberId = userDetails.getMemberId();
        PostUpdateResponse response = postService.updatePost(memberId, postId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer postId
    ) {
        Integer memberId = userDetails.getMemberId();
        postService.deletePost(memberId, postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}