package kr.adapterz.community.auth.service;

import java.util.Date;
import kr.adapterz.community.auth.dto.LoginRequest;
import kr.adapterz.community.auth.dto.LoginResponse;
import kr.adapterz.community.auth.jwt.JWTUtil;
import kr.adapterz.community.auth.refresh.entity.RefreshEntity;
import kr.adapterz.community.auth.refresh.repository.RefreshRepository;
import kr.adapterz.community.domain.image.entity.Image;
import kr.adapterz.community.domain.image.repository.ImageRepository;
import kr.adapterz.community.domain.member.dto.JoinRequest;
import kr.adapterz.community.domain.member.dto.MemberJoinResponse;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.BadRequestException;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final RefreshRepository refreshRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    /**
     * 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorCode.LOGIN_FAILED));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorCode.LOGIN_FAILED);
        }

        // 3. Access 토큰 생성 (30분)
        String accessToken = jwtUtil.createJwt(
                member.getEmail(),
                member.getRole(),
                30 * 60 * 1000L
        );

        // 4. Refresh 토큰 생성 (7일)
        String refreshToken = jwtUtil.createRefreshToken(
                member.getEmail(),
                7 * 24 * 60 * 60 * 1000L
        );

        // 5. Refresh 토큰 DB 저장
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .email(member.getEmail())
                .refresh(refreshToken)
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L).toString())
                .build();

        refreshRepository.save(refreshEntity);

        log.info("로그인 성공: email={}", member.getEmail());

        // 6. 응답 생성
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }

    /**
     * 회원가입
     */
    @Transactional
    public MemberJoinResponse join(JoinRequest request) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(ErrorCode.EMAIL_DUPLICATED);
        }

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new BadRequestException(ErrorCode.NICKNAME_DUPLICATED);
        }

        // 프로필 이미지 조회
        Image image = imageRepository.findById(request.getProfileImageId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));

        // 사용자 생성
        Member member = Member.createMember(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                image,
                "ROLE_USER"
        );

        Member savedMember = memberRepository.save(member);

        log.info("회원가입 성공: email={}", savedMember.getEmail());

        return MemberJoinResponse.from(savedMember);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String refreshToken) {
        // Refresh 토큰 DB에서 삭제
        refreshRepository.deleteByRefresh(refreshToken);
        log.info("로그아웃 완료");
    }
}