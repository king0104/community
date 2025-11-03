package kr.adapterz.community.auth.service;

import kr.adapterz.community.auth.dto.LoginRequest;
import kr.adapterz.community.auth.dto.LoginResponse;
import kr.adapterz.community.auth.jwt.JwtUtil;
import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import kr.adapterz.community.auth.refresh.repository.RefreshRepository;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.BadRequestException;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 비밀번호 검증 (BCrypt)
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorCode.INCORRECT_PASSWORD);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtUtil.createRefreshToken(member.getEmail());

        // 4. Refresh Token DB 저장 (기존 토큰이 있으면 업데이트)
        saveOrUpdateRefreshToken(member.getEmail(), refreshToken);

        return LoginResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse reissue(String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 2. DB에 저장된 Refresh Token 확인
        RefreshEntity refreshEntity = refreshRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException(ErrorCode.REFRESH_TOKEN_INVALID));

        // 3. 만료 확인
        if (refreshEntity.isExpired()) {
            refreshRepository.delete(refreshEntity);
            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 4. 회원 조회
        String email = jwtUtil.getEmail(refreshToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 5. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.createAccessToken(member.getEmail(), member.getRole());

        // 6. 새로운 Refresh Token 생성 (Refresh Token Rotation)
        String newRefreshToken = jwtUtil.createRefreshToken(member.getEmail());

        // 7. 기존 Refresh Token 업데이트
        refreshEntity.updateRefreshToken(newRefreshToken, LocalDateTime.now().plusDays(14));

        return LoginResponse.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String email) {
        // Refresh Token 삭제
        refreshRepository.deleteByEmail(email);
    }

    private void saveOrUpdateRefreshToken(String email, String refreshToken) {
        LocalDateTime expiration = LocalDateTime.now().plusDays(14);

        RefreshEntity refreshEntity = refreshRepository.findByEmail(email)
                .map(entity -> {
                    entity.updateRefreshToken(refreshToken, expiration);
                    return entity;
                })
                .orElse(RefreshEntity.createRefreshToken(email, refreshToken, expiration));

        refreshRepository.save(refreshEntity);
    }
}
