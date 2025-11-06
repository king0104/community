package kr.adapterz.community.auth.resolver;

import jakarta.servlet.http.HttpServletRequest;
import kr.adapterz.community.auth.annotation.LoginMember;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.UnAuthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @LoginMember 어노테이션이 붙은 파라미터에 memberId 자동 주입
 */
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @LoginMember 어노테이션이 있고, Integer 타입인 경우
        return parameter.hasParameterAnnotation(LoginMember.class)
                && parameter.getParameterType().equals(Integer.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Integer memberId = (Integer) request.getAttribute("memberId");

        // required 속성 확인
        LoginMember loginMember = parameter.getParameterAnnotation(LoginMember.class);
        boolean required = loginMember != null && loginMember.required();

        // 필수인데 없으면 예외
        if (required && memberId == null) {
            throw new UnAuthorizedException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        return memberId;  // null 가능 (required=false인 경우)
    }
}