package kr.adapterz.community.config;

import kr.adapterz.community.auth.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 필터 설정
 * - AuthenticationFilter를 등록하여 모든 API 요청에 대해 인증 체크
 */
@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authenticationFilter);
        registrationBean.addUrlPatterns("/api/v1/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}