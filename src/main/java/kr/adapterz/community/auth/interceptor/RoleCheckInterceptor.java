package kr.adapterz.community.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import kr.adapterz.community.auth.annotation.RequireRole;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.ForbiddenException;
import kr.adapterz.community.global.exception.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 권한 체크 인터셉터
 * - @RequireRole 어노테이션 확인
 * - 사용자 권한 검증
 */
@Slf4j
@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        // HandlerMethod가 아니면 통과
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 1. 메서드 레벨에서 @RequireRole 확인
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);

        // 2. 없으면 클래스 레벨에서 확인
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // 3. @RequireRole이 없으면 통과
        if (requireRole == null) {
            return true;
        }

        // 4. request에서 회원 정보 가져오기
        Integer memberId = (Integer) request.getAttribute("memberId");
        String memberRole = (String) request.getAttribute("memberRole");

        // 5. 인증 안 된 경우
        if (memberId == null || memberRole == null) {
            throw new UnAuthorizedException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        // 6. 필요한 권한 목록
        String[] allowedRoles = requireRole.value();

        // 7. 권한 체크
        boolean hasRole = Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equals(memberRole));

        if (!hasRole) {
            log.warn("권한 부족: memberId={}, required={}, actual={}",
                    memberId, Arrays.toString(allowedRoles), memberRole);
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        log.debug("권한 체크 성공: memberId={}, role={}", memberId, memberRole);
        return true;
    }
}