package kr.adapterz.community.domain.post.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.domain.post.dto.PostCreateRequest;
import kr.adapterz.community.domain.post.dto.PostCreateResponse;
import kr.adapterz.community.domain.post.dto.PostDetailResponse;
import kr.adapterz.community.domain.post.dto.PostListPageResponse;
import kr.adapterz.community.domain.post.dto.PostUpdateRequest;
import kr.adapterz.community.domain.post.dto.PostUpdateResponse;
import kr.adapterz.community.domain.post.service.PostService;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(
            @Valid @RequestBody PostCreateRequest request
    ) {
        Integer memberId = getMemberId();
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
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable Integer postId
    ) {
        Integer memberId = getMemberIdOrNull();
        PostDetailResponse response = postService.getPostDetail(postId, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostUpdateResponse> updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        Integer memberId = getMemberId();
        PostUpdateResponse response = postService.updatePost(memberId, postId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer postId
    ) {
        Integer memberId = getMemberId();
        postService.deletePost(memberId, postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    private Integer getMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return member.getId();
    }

    private Integer getMemberIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String email = authentication.getName();
        return memberRepository.findByEmail(email)
                .map(Member::getId)
                .orElse(null);
    }

}