package kr.adapterz.community.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.adapterz.community.auth.session.SessionDto;
import kr.adapterz.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증 필터
 * - 모든 요청에 대해 세션 기반 인증 체크
 * - 인증 정보를 request attribute에 저장하여 인터셉터와 컨트롤러에서 사용 가능하도록 함
 */
@Component
@RequiredArgsConstructor
@Order(1)
public class AuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 공개 경로는 패스
        if (isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 세션에서 인증 정보 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 인증 실패 - 401 반환 - 전역에서 예외 처리가 불가능함
            sendErrorResponse(response, ErrorCode.UNAUTHORIZED);
            return;
        }

        // 인증 성공 - request에 인증 정보 저장 (인터셉터/컨트롤러에서 사용)
        SessionDto sessionDto = (SessionDto) session.getAttribute("loginMember");
        request.setAttribute("memberId", sessionDto.getMemberId());
        request.setAttribute("role", sessionDto.getRole());
        request.setAttribute("authenticated", true);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String requestURI) {
        return requestURI.equals("/api/v1/auth/login")
                || requestURI.equals("/api/v1/members")
                || requestURI.startsWith("/api/v1/images")
                || requestURI.equals("/privacy") // 정적 파일도 관리 가능
                || requestURI.equals("/terms")
                || requestURI.startsWith("/api/v1/posts") && !requestURI.contains("/comments");
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", String.valueOf(errorCode.getStatus()));
        errorResponse.put("message", errorCode.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}