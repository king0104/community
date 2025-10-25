package kr.adapterz.community.domain.comment.service;

import kr.adapterz.community.domain.comment.dto.CommentCreateRequest;
import kr.adapterz.community.domain.comment.dto.CommentCreateResponse;
import kr.adapterz.community.domain.comment.dto.CommentResponse;
import kr.adapterz.community.domain.comment.dto.CommentUpdateRequest;
import kr.adapterz.community.domain.comment.entity.Comment;
import kr.adapterz.community.domain.comment.repository.CommentRepository;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.ForbiddenException;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentCreateResponse createComment(Integer postId, Integer memberId, CommentCreateRequest request) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 부모 댓글 조회 (대댓글인 경우)
        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.PARENT_COMMENT_NOT_FOUND));
        }

        // 댓글 생성
        Comment comment;
        if (parent != null) {
            comment = Comment.createComment(post, member, parent, request.getContent());
        } else {
            comment = Comment.createComment(post, member, request.getContent());
        }

        // 댓글 저장
        Comment savedComment = commentRepository.save(comment);

        return CommentCreateResponse.of(savedComment);
    }

    public List<CommentResponse> getCommentsByPostId(Integer postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findByPostIdWithMember(postId);

        return comments.stream()
                .map(CommentResponse::of)
                .toList();
    }

    @Transactional
    public CommentResponse updateComment(Integer memberId, Integer commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        comment.updateContent(request.getContent());

        return CommentResponse.of(comment);
    }

}
