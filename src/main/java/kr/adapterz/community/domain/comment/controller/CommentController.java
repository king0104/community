package kr.adapterz.community.domain.comment.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.domain.comment.dto.CommentCreateRequest;
import kr.adapterz.community.domain.comment.dto.CommentCreateResponse;
import kr.adapterz.community.domain.comment.dto.CommentResponse;
import kr.adapterz.community.domain.comment.dto.CommentUpdateRequest;
import kr.adapterz.community.domain.comment.service.CommentService;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<CommentCreateResponse> createComment(
            @PathVariable Integer postId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Integer memberId = getMemberId();
        CommentCreateResponse response = commentService.createComment(postId, memberId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Integer postId) {
        List<CommentResponse> responses = commentService.getCommentsByPostId(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responses);
    }

    @PatchMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        Integer memberId = getMemberId();
        CommentResponse response = commentService.updateComment(memberId, commentId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId
    ) {
        Integer memberId = getMemberId();
        commentService.deleteComment(memberId, commentId);

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

}