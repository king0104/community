package kr.adapterz.community.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.adapterz.community.auth.jwt.JWTUtil;
import kr.adapterz.community.auth.jwt.JwtConstants;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import kr.adapterz.community.global.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터
 * - Access Token 검증
 * - 회원 정보 조회
 * - request에 memberId, memberRole 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 인증이 필요 없는 경로는 통과
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. OPTIONS 요청은 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. 헤더에서 Access Token 추출
            String accessToken = request.getHeader(JwtConstants.HEADER_NAME_ACCESS);

            if (accessToken == null || accessToken.isEmpty()) {
                throw new UnAuthorizedException(ErrorCode.TOKEN_MISSING);
            }

            // 4. 토큰 만료 확인
            if (jwtUtil.isExpired(accessToken)) {
                throw new UnAuthorizedException(ErrorCode.TOKEN_EXPIRED);
            }

            // 5. 토큰에서 이메일 추출
            String email = jwtUtil.getEmail(accessToken);

            // 6. DB에서 회원 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

            // 7. request에 회원 정보 저장
            request.setAttribute("memberId", member.getId());
            request.setAttribute("memberEmail", member.getEmail());
            request.setAttribute("memberRole", member.getRole());

            log.debug("JWT 인증 성공: memberId={}, email={}", member.getId(), email);

            // 8. 다음 필터로
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰: {}", e.getMessage());
            throw new UnAuthorizedException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.warn("유효하지 않은 토큰: {}", e.getMessage());
            throw new UnAuthorizedException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 인증이 필요 없는 공개 경로인지 확인
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/join")
                || path.startsWith("/api/v1/auth/refresh")
                || path.equals("/api/v1/posts")  // 게시글 목록 조회는 공개
                || (path.startsWith("/api/v1/posts/") && path.matches("/api/v1/posts/\\d+"));  // 게시글 상세는 공개
    }
}