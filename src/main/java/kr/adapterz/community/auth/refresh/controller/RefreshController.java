package kr.adapterz.community.auth.refresh.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import kr.adapterz.community.auth.cookie.CookieUtil;
import kr.adapterz.community.auth.jwt.JWTUtil;
import kr.adapterz.community.auth.jwt.JwtConstants;
import kr.adapterz.community.auth.refresh.dto.ReissueTokenDto;
import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import kr.adapterz.community.auth.refresh.repository.RefreshRepository;
import kr.adapterz.community.auth.refresh.service.RefreshService;
import kr.adapterz.community.global.exception.BadRequestException;
import kr.adapterz.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RefreshController {

    private final RefreshService refreshService;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<Void> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. 쿠키에서 Refresh Token 추출
        String refreshToken = cookieUtil
                .getCookieValue(request, JwtConstants.COOKIE_NAME_REFRESH)
                .orElseThrow(() -> new BadRequestException(ErrorCode.REFRESH_TOKEN_MISSED));

        // 2. 토큰 재발급
        ReissueTokenDto reissueTokenDto = refreshService.reissueTokens(refreshToken);

        // 3. 응답에 새로운 토큰 설정
        response.setHeader(JwtConstants.HEADER_NAME_ACCESS, reissueTokenDto.getAccessToken());
        response.addCookie(
                cookieUtil.createCookie(
                        JwtConstants.COOKIE_NAME_REFRESH,
                        reissueTokenDto.getRefreshToken()
                )
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}