package kr.adapterz.community.member.service;

import kr.adapterz.community.member.dto.JoinRequest;
import kr.adapterz.community.member.entity.Member;
import kr.adapterz.community.member.repository.MemberRepository;
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

}
