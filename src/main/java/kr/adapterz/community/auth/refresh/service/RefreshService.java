package kr.adapterz.community.auth.refresh.service;

import java.util.Date;
import kr.adapterz.community.auth.jwt.JWTUtil;
import kr.adapterz.community.auth.jwt.JwtConstants;
import kr.adapterz.community.auth.refresh.dto.ReissueTokenDto;
import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import kr.adapterz.community.auth.refresh.repository.RefreshRepository;
import kr.adapterz.community.global.exception.BadRequestException;
import kr.adapterz.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    /**
     * Refresh Token을 검증하고 새로운 Access/Refresh Token을 발급
     */
    public ReissueTokenDto reissueTokens(String refreshToken) {
        // 1. 토큰 만료 검증
        jwtUtil.validateTokenExpiration(refreshToken);

        // 2. 토큰 카테고리 검증 (refresh 토큰인지 확인)
        validateTokenCategory(refreshToken);

        // 3. DB에 저장된 토큰인지 확인
        validateTokenExistsInDB(refreshToken);

        // 4. 토큰에서 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 5. 새로운 토큰 생성
        String newAccessToken = jwtUtil.createJwt(
                JwtConstants.TOKEN_CATEGORY_ACCESS,
                username,
                role,
                JwtConstants.ACCESS_TOKEN_EXPIRATION
        );

        String newRefreshToken = jwtUtil.createJwt(
                JwtConstants.TOKEN_CATEGORY_REFRESH,
                username,
                role,
                JwtConstants.REFRESH_TOKEN_EXPIRATION
        );

        // 6. 기존 Refresh Token 삭제 및 새로운 Token 저장
        rotateRefreshToken(refreshToken, username, newRefreshToken);

        return ReissueTokenDto.of(newAccessToken, newRefreshToken);
    }

    /**
     * 토큰 카테고리가 refresh인지 검증
     */
    private void validateTokenCategory(String token) {
        String category = jwtUtil.getCategory(token);
        if (!"refresh".equals(category)) {
            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    /**
     * DB에 토큰이 존재하는지 검증
     */
    private void validateTokenExistsInDB(String token) {
        if (!refreshRepository.existsByRefresh(token)) {
            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    /**
     * Refresh Token 교체 (Refresh Token Rotation)
     */
    private void rotateRefreshToken(String oldToken, String username, String newToken) {
        // 기존 토큰 삭제
        refreshRepository.deleteByRefresh(oldToken);
        // 새로운 토큰 저장
        saveRefreshToken(username, newToken, JwtConstants.REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * Refresh Token을 DB에 저장
     */
    private void saveRefreshToken(String username, String refreshToken, Long expiredMs) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = RefreshEntity.createRefreshEntity(
                username,
                refreshToken,
                expirationDate.toString()
        );

        refreshRepository.save(refreshEntity);
    }

}
