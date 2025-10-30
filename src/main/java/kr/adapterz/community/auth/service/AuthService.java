package kr.adapterz.community.auth.service;

import jakarta.transaction.Transactional;
import kr.adapterz.community.auth.dto.LoginRequest;
import kr.adapterz.community.auth.dto.LoginResponse;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.dto.ExceptionResponse;
import kr.adapterz.community.global.exception.BadRequestException;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import kr.adapterz.community.global.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.isWithdrawn()) {
            throw new BadRequestException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        if (!passwordEncoder.matches(member.getPassword(), request.getPassword())) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED);
        }

        return member;
    }
}
