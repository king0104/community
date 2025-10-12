package kr.adapterz.community.domain.member.service;

import kr.adapterz.community.domain.member.dto.JoinRequest;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String nickname = joinRequest.getNickname();
        String profile_image_url = joinRequest.getProfile_image_url();
        String role = joinRequest.getRole();

        Boolean isExist = memberRepository.existsByEmail(email);

        if (isExist) {
            return;
        }

        Member member = Member.createMember(
                email,
                bCryptPasswordEncoder.encode(password),
                nickname,
                profile_image_url,
                role
        );

        memberRepository.save(member);

    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

}
