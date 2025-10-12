package kr.adapterz.community.auth.service;

import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //DB에서 조회
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
        return new CustomUserDetails(member);
    }

}