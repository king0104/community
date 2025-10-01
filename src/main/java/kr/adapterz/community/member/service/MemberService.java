package kr.adapterz.community.member.service;

import java.nio.file.AccessDeniedException;
import kr.adapterz.community.member.dto.JoinRequest;
import kr.adapterz.community.member.dto.MemberPatchRequest;
import kr.adapterz.community.member.dto.MemberPatchResponse;
import kr.adapterz.community.member.dto.MemberRequest;
import kr.adapterz.community.member.entity.Member;
import kr.adapterz.community.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public void join(JoinRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 유저가 존재합니다");
        }

        Member member = Member.createMember(
                request.getEmail(),
                bCryptPasswordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getProfile_image_url(),
                request.getRole()
        );

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Boolean existMember(MemberRequest request) {
        return memberRepository.existsByEmail(request.getEmail());
    }

    @Transactional
    public MemberPatchResponse updateMember(MemberPatchRequest request) throws AccessDeniedException {
        String sessionUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUserName.equals(request.getEmail())) {
            throw new AccessDeniedException("본인 계정만 수정 가능");
        }

        Member member = memberRepository.findByEmailAndIsLockAndIsSocial(
                request.getEmail(),
                false,
                false
        ).orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));

        member.updateMember(request);

        return MemberPatchResponse.createMemberPatchResponse(
                memberRepository.save(member).getId()
        );
    }

    // 자체 로그인
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmailAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return Member.createMember(
                member.getEmail(),
                member.getPassword(),
                member.getNickname(),
                member.getProfile_image_url(),
                member.getRole()
        );
    }

}
