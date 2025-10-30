package kr.adapterz.community.auth.service;

import io.jsonwebtoken.Jwts;
import kr.adapterz.community.auth.controller.AuthController;
import kr.adapterz.community.auth.dto.LoginRequest;
import kr.adapterz.community.auth.dto.LoginResponse;
import kr.adapterz.community.auth.jwt.JwtUtil;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.BadRequestException;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        // 1. 비밀번호 일치하는지 검증
        // 2. 로그인 되면 JWT 만들어서 주기
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.getPassword().equals(request.getPassword())) {
            throw new BadRequestException(ErrorCode.INCORRECT_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(member.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(member.getEmail());

        return LoginResponse.of(accessToken, refreshToken);

    }



}
