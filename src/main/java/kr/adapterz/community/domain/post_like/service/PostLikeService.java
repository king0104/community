package kr.adapterz.community.domain.post_like.service;

import java.util.Optional;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.domain.post_stats.entity.PostStats;
import kr.adapterz.community.domain.post_like.dto.PostLikeResponse;
import kr.adapterz.community.domain.post_like.entity.PostLike;
import kr.adapterz.community.domain.post_like.repository.PostLikeRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
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
        // 1. Post와 PostStats 조회 (Pessimistic Lock)
        Post post = postRepository.findByIdWithStatsForUpdate(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        PostStats postStats = post.getPostStats();

        // 2. 좋아요 중복 확인 (조회 쿼리 1회로 최적화)
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId);
        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 현재 상태 반환 (멱등성)
            return PostLikeResponse.of(true, postStats.getLikeCount());
        }

        // 3. Member 조회 (좋아요 생성이 필요한 경우에만)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 4. 좋아요 생성 및 카운트 증가
        PostLike postLike = PostLike.createPostLike(post, member);
        postLikeRepository.save(postLike);
        postStats.increaseLikeCount();

        return PostLikeResponse.of(true, postStats.getLikeCount());
    }

    @Transactional
    public PostLikeResponse unlike(Integer postId, Integer memberId) {
        // 1. Post와 PostStats 조회 (Pessimistic Lock)
        Post post = postRepository.findByIdWithStatsForUpdate(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        PostStats postStats = post.getPostStats();

        // 2. 좋아요 조회 및 삭제
        Optional<PostLike> postLikeOptional = postLikeRepository.findByPostIdAndMemberId(postId, memberId);
        if (postLikeOptional.isPresent()) {
            PostLike postLike = postLikeOptional.get();
            postLikeRepository.delete(postLike);
            postStats.decreaseLikeCount();
        }
        // 좋아요가 없어도 예외를 던지지 않음 (멱등성)

        return PostLikeResponse.of(false, postStats.getLikeCount());
    }
}
