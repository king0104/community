package kr.adapterz.community.domain.post.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.adapterz.community.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.adapterz.community.domain.post.entity.QPost.post;
import static kr.adapterz.community.domain.member.entity.QMember.member;
import static kr.adapterz.community.domain.post_stats.entity.QPostStats.postStats;
import static kr.adapterz.community.domain.image.entity.QImage.image;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findPostsWithCursor(Integer cursor, int size) {
        return queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .join(post.postStats, postStats).fetchJoin()
                .join(member.image, image).fetchJoin()
                .where(
                        post.isDeleted.eq(false),
                        cursorCondition(cursor)
                )
                .orderBy(post.id.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public Optional<Post> findPostDetailById(Integer postId) {
        Post result = queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .join(post.postStats, postStats).fetchJoin()
                .join(member.image, image).fetchJoin()
                .leftJoin(post.images).fetchJoin()
                .where(
                        post.id.eq(postId),
                        post.isDeleted.eq(false)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression cursorCondition(Integer cursor) {
        return cursor != null ? post.id.lt(cursor) : null;
    }

}