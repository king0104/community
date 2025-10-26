package kr.adapterz.community.domain.post.service;

import kr.adapterz.community.domain.image.entity.Image;
import kr.adapterz.community.domain.image.repository.ImageRepository;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.domain.post.dto.PostCreateRequest;
import kr.adapterz.community.domain.post.dto.PostCreateResponse;
import kr.adapterz.community.domain.post.dto.PostDetailResponse;
import kr.adapterz.community.domain.post.dto.PostListPageResponse;
import kr.adapterz.community.domain.post.dto.PostListResponse;
import kr.adapterz.community.domain.post.dto.PostUpdateRequest;
import kr.adapterz.community.domain.post.dto.PostUpdateResponse;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.domain.post_like.repository.PostLikeRepository;
import kr.adapterz.community.domain.post_stats.entity.PostStats;
import kr.adapterz.community.global.exception.CustomException;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.ForbiddenException;
import kr.adapterz.community.global.exception.NotFoundException;
import kr.adapterz.community.global.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostCreateResponse createPost(Integer memberId, PostCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        PostStats postStats = PostStats.createPostStats();

        // 이미지 찾기
        List<Image> images = new ArrayList<>();
        if (request.getImageIds() != null && !request.getImageIds().isEmpty()) {
            images = imageRepository.findAllById(request.getImageIds());
            if (images.size() != request.getImageIds().size()) {
                throw new NotFoundException(ErrorCode.IMAGE_NOT_FOUND);
            }
        }
        // 게시글 생성
        Post post = Post.createPost(
                member,
                request.getTitle(),
                request.getContent(),
                postStats,
                images // images null 가능
        );
        // 게시글 저장
        Post savedPost = postRepository.save(post);

        return PostCreateResponse.of(savedPost);
    }

    public PostListPageResponse getPostList(Integer cursor, int size) {
        List<Post> posts = postRepository.findPostsWithCursor(cursor, size);

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        List<PostListResponse> postResponses = posts.stream()
                .map(PostListResponse::of)
                .toList();

        Integer nextCursor = hasNext && !posts.isEmpty()
                ? posts.get(posts.size() - 1).getId()
                : null;

        return PostListPageResponse.of(postResponses, nextCursor, hasNext);
    }

    @Transactional
    public PostDetailResponse getPostDetail(Integer postId, Integer memberId) {
        // 1. 게시글 조회
        Post post = postRepository.findPostDetailById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        // 2. 조회수 증가
        post.getPostStats().increaseViewCount();

        // 3. 좋아요 여부 확인
        boolean isLikedByMe = postLikeRepository.existsByPostIdAndMemberId(postId, memberId);

        return PostDetailResponse.of(post, isLikedByMe);
    }

    @Transactional
    public PostUpdateResponse updatePost(Integer memberId, Integer postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        List<Image> images = null;
        if (request.getImageIds() != null) {
            images = new ArrayList<>();
            for (Integer imageId : request.getImageIds()) {
                Image image = imageRepository.findById(imageId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));
                images.add(image);
            }
        }

        post.updatePost(request.getTitle(), request.getContent(), images);

        return PostUpdateResponse.of(post);
    }

    @Transactional
    public void deletePost(Integer memberId, Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        post.delete();
    }

}