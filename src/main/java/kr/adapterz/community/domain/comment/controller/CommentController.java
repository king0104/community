package kr.adapterz.community.domain.comment.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.auth.service.CustomUserDetails;
import kr.adapterz.community.domain.comment.dto.CommentCreateRequest;
import kr.adapterz.community.domain.comment.dto.CommentCreateResponse;
import kr.adapterz.community.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentCreateResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Integer memberId = userDetails.getMemberId();
        CommentCreateResponse response = commentService.createComment(memberId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
