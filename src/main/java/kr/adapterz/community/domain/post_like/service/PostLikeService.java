package kr.adapterz.community.domain.post_like.service;

import java.util.Optional;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.domain.post_like.dto.PostLikeResponse;
import kr.adapterz.community.domain.post_like.entity.PostLike;
import kr.adapterz.community.domain.post_like.repository.PostLikeRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostLikeResponse like(Integer postId, Integer memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        boolean alreadyLiked = postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
        if (!alreadyLiked) {
            try {
                PostLike newLike = PostLike.createPostLike(post, member);
                postLikeRepository.save(newLike);
            } catch (DataIntegrityViolationException e) {
                // 유니크 제약으로 인한 동시성 충돌 시, 이미 좋아요로 간주
            }
        }

        long likeCount = postLikeRepository.countByPostId(postId);

        return PostLikeResponse.of(true, likeCount);
    }
}
