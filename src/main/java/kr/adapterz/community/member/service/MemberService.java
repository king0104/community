package kr.adapterz.community.member.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.adapterz.community.auth.RoleType;
import kr.adapterz.community.auth.SocialProviderType;
import kr.adapterz.community.auth.dto.CustomOAuth2User;
import kr.adapterz.community.auth.jwt.service.JwtService;
import kr.adapterz.community.member.dto.JoinRequest;
import kr.adapterz.community.member.dto.MemberPatchRequest;
import kr.adapterz.community.member.dto.MemberPatchResponse;
import kr.adapterz.community.member.dto.MemberRequest;
import kr.adapterz.community.member.entity.Member;
import kr.adapterz.community.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MemberService extends DefaultOAuth2UserService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
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
                member.getProfile_image_url()
        );
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 부모 메소드 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 데이터
        Map<String, Object> attributes;
        List<GrantedAuthority> authorities;

        String username;
        String role = RoleType.MEMBER.name();
        String email;
        String nickname;
        SocialProviderType socialProviderType;

        // provider 제공자별 데이터 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        if (registrationId.equals(SocialProviderType.NAVER.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            username = registrationId + "_" + attributes.get("id");
            email = attributes.get("email").toString();
            nickname = attributes.get("nickname").toString();
            socialProviderType = SocialProviderType.NAVER;

        } else if (registrationId.equals(SocialProviderType.GOOGLE.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes();
            username = registrationId + "_" + attributes.get("sub");
            email = attributes.get("email").toString();
            nickname = attributes.get("name").toString();
            socialProviderType = SocialProviderType.GOOGLE;

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        // 데이터베이스 조회 -> 존재하면 업데이트, 없으면 신규 가입
        Optional<Member> member = memberRepository.findByEmailAndIsSocial(email, true);
        if (member.isEmpty()) {
            // 신규 유저 추가
            Member newMember = Member.createSocialMember(
                    email,
                    nickname,
                    socialProviderType,
                    ""
            );

            memberRepository.save(newMember);
        }

        authorities = List.of(new SimpleGrantedAuthority(role));

        return new CustomOAuth2User(attributes, authorities, username);
    }

    // 자체/소셜 로그인 회원 탈퇴
    @Transactional
    public void deleteUser(UserRequestDTO dto) throws AccessDeniedException {

        // 본인 및 어드민만 삭제 가능 검증
        SecurityContext context = SecurityContextHolder.getContext();
        String sessionUsername = context.getAuthentication().getName();
        String sessionRole = context.getAuthentication().getAuthorities().iterator().next().getAuthority();

        boolean isOwner = sessionUsername.equals(dto.getUsername());
        boolean isAdmin = sessionRole.equals("ROLE_"+UserRoleType.ADMIN.name());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }

        // 유저 제거
        userRepository.deleteByUsername(dto.getUsername());

        // Refresh 토큰 제거
        jwtService.removeRefreshUser(dto.getUsername());
    }

}
