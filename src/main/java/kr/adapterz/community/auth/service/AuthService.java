package kr.adapterz.community.auth.service;

import kr.adapterz.community.domain.image.entity.Image;
import kr.adapterz.community.domain.image.repository.ImageRepository;
import kr.adapterz.community.domain.member.dto.JoinRequest;
import kr.adapterz.community.domain.member.dto.MemberJoinResponse;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;


}
