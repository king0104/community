package kr.adapterz.community.domain.post.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.auth.annotation.LoginMember;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 작성
     */
    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(
            @LoginMember Integer memberId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostCreateResponse response = postService.createPost(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 목록 조회 (인증 불필요)
     */
    @GetMapping
    public ResponseEntity<PostListPageResponse> getPostList(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        PostListPageResponse response = postService.getPostList(cursor, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회 (로그인 선택)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable Integer postId,
            @LoginMember(required = false) Integer memberId
    ) {
        PostDetailResponse response = postService.getPostDetail(postId, memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<PostUpdateResponse> updatePost(
            @LoginMember Integer memberId,
            @PathVariable Integer postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        PostUpdateResponse response = postService.updatePost(memberId, postId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @LoginMember Integer memberId,
            @PathVariable Integer postId
    ) {
        postService.deletePost(memberId, postId);
        return ResponseEntity.ok().build();
    }
}