package kr.adapterz.community.domain.comment.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.auth.annotation.LoginMember;
import kr.adapterz.community.domain.comment.dto.CommentCreateRequest;
import kr.adapterz.community.domain.comment.dto.CommentCreateResponse;
import kr.adapterz.community.domain.comment.dto.CommentResponse;
import kr.adapterz.community.domain.comment.dto.CommentUpdateRequest;
import kr.adapterz.community.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     */
    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<CommentCreateResponse> createComment(
            @PathVariable Integer postId,
            @LoginMember Integer memberId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentCreateResponse response = commentService.createComment(postId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 목록 조회 (인증 불필요)
     */
    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Integer postId) {
        List<CommentResponse> responses = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @LoginMember Integer memberId,
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        CommentResponse response = commentService.updateComment(memberId, commentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @LoginMember Integer memberId,
            @PathVariable Integer postId,
            @PathVariable Integer commentId
    ) {
        commentService.deleteComment(memberId, commentId);
        return ResponseEntity.ok().build();
    }
}