package kr.adapterz.community.auth.refresh.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.Date;
import kr.adapterz.community.auth.jwt.JWTUtil;
import kr.adapterz.community.auth.refresh.dto.ReissueTokenDto;
import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import kr.adapterz.community.auth.refresh.repository.RefreshRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Transactional
    public ReissueTokenDto reissueTokens(String refreshToken) {
        // 1. Refresh 토큰 검증
        try {
            if (jwtUtil.isExpired(refreshToken)) {
                throw new UnAuthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new UnAuthorizedException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 2. DB에 저장된 토큰인지 확인
        RefreshEntity refreshEntity = refreshRepository.findByRefresh(refreshToken)
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 3. 새로운 Access 토큰 생성
        String newAccessToken = jwtUtil.createJwt(email, role, 30 * 60 * 1000L);

        // 4. 새로운 Refresh 토큰 생성
        String newRefreshToken = jwtUtil.createRefreshToken(email, 7 * 24 * 60 * 60 * 1000L);

        // 5. 기존 Refresh 토큰 삭제
        refreshRepository.delete(refreshEntity);

        // 6. 새로운 Refresh 토큰 저장
        RefreshEntity newRefreshEntity = RefreshEntity.builder()
                .email(email)
                .refresh(newRefreshToken)
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L).toString())
                .build();

        refreshRepository.save(newRefreshEntity);

        log.info("토큰 재발급 완료: email={}", email);

        return ReissueTokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}