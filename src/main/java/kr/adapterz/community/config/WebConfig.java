package kr.adapterz.community.config;

import java.util.List;
import kr.adapterz.community.auth.interceptor.RoleCheckInterceptor;
import kr.adapterz.community.auth.resolver.LoginMemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 * - Interceptor 등록
 * - ArgumentResolver 등록
 * - CORS 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RoleCheckInterceptor roleCheckInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    /**
     * 인터셉터 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleCheckInterceptor)
                .addPathPatterns("/api/**");  // 모든 API에 적용
    }

    /**
     * ArgumentResolver 등록
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    /**
     * CORS 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("access", "refresh")  // 응답 헤더 노출
                .allowCredentials(true)
                .maxAge(3600);
    }
}