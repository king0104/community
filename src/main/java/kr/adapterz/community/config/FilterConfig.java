package kr.adapterz.community.config;

import kr.adapterz.community.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter 등록 설정
 */
@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtAuthenticationFilter);
        registrationBean.addUrlPatterns("/api/*");  // /api로 시작하는 모든 요청
        registrationBean.setOrder(1);  // 필터 순서

        return registrationBean;
    }
}