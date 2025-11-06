package kr.adapterz.community.auth.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import kr.adapterz.community.auth.jwt.JwtConstants;
import org.springframework.stereotype.Component;

/**
 * 쿠키 생성 및 조회 유틸리티
 */
@Component
public class CookieUtil {

    /**
     * 쿠키 생성
     */
    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(7 * 24 * 60 * 60);  // 7일
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);  // HTTPS에서만 전송 (프로덕션에서 활성화)
        return cookie;
    }

    /**
     * 요청에서 특정 쿠키 값 조회
     */
    public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}