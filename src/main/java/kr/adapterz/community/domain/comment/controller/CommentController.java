package kr.adapterz.community.domain.comment.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.auth.service.CustomUserDetails;
import kr.adapterz.community.domain.comment.dto.CommentCreateRequest;
import kr.adapterz.community.domain.comment.dto.CommentCreateResponse;
import kr.adapterz.community.domain.comment.dto.CommentResponse;
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

}
