package kr.adapterz.community.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.community.auth.annotation.LoginMember;
import kr.adapterz.community.auth.cookie.CookieUtil;
import kr.adapterz.community.auth.dto.LoginRequest;
import kr.adapterz.community.auth.dto.LoginResponse;
import kr.adapterz.community.auth.jwt.JwtConstants;
import kr.adapterz.community.auth.service.AuthService;
import kr.adapterz.community.domain.member.dto.JoinRequest;
import kr.adapterz.community.domain.member.dto.MemberJoinResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.login(request);

        // Access 토큰은 헤더에
        response.setHeader(JwtConstants.HEADER_NAME_ACCESS, loginResponse.getAccessToken());

        // Refresh 토큰은 HttpOnly 쿠키에
        Cookie refreshCookie = cookieUtil.createCookie(
                JwtConstants.COOKIE_NAME_REFRESH,
                loginResponse.getRefreshToken()
        );
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<MemberJoinResponse> join(
            @Valid @RequestBody JoinRequest request
    ) {
        MemberJoinResponse response = authService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @LoginMember Integer memberId,  // @LoginMember로 자동 주입!
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 쿠키에서 Refresh 토큰 가져오기
        String refreshToken = cookieUtil
                .getCookieValue(request, JwtConstants.COOKIE_NAME_REFRESH)
                .orElse(null);

        // DB에서 Refresh 토큰 삭제
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        // 쿠키 삭제
        Cookie cookie = cookieUtil.createCookie(JwtConstants.COOKIE_NAME_REFRESH, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}