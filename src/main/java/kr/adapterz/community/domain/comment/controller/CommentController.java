package kr.adapterz.community.domain.comment.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.domain.comment.dto.CommentCreateRequest;
import kr.adapterz.community.domain.comment.dto.CommentCreateResponse;
import kr.adapterz.community.domain.comment.dto.CommentResponse;
import kr.adapterz.community.domain.comment.dto.CommentUpdateRequest;
import kr.adapterz.community.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<CommentCreateResponse> createComment(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Integer memberId = userDetails.getMemberId();
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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        Integer memberId = userDetails.getMemberId();
        CommentResponse response = commentService.updateComment(memberId, commentId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer postId,
            @PathVariable Integer commentId
    ) {
        Integer memberId = userDetails.getMemberId();
        commentService.deleteComment(memberId, commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
