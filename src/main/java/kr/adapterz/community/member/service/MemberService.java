package kr.adapterz.community.member.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import kr.adapterz.community.auth.RoleType;
import kr.adapterz.community.auth.SocialProviderType;
import kr.adapterz.community.member.dto.JoinRequest;
import kr.adapterz.community.member.dto.MemberPatchRequest;
import kr.adapterz.community.member.dto.MemberPatchResponse;
import kr.adapterz.community.member.dto.MemberRequest;
import kr.adapterz.community.member.entity.Member;
import kr.adapterz.community.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        // provider 제공자별 데이터 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        if (registrationId.equals(SocialProviderType.NAVER.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            username = registrationId + "_" + attributes.get("id");
            email = attributes.get("email").toString();
            nickname = attributes.get("nickname").toString();

        } else if (registrationId.equals(SocialProviderType.GOOGLE.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes();
            username = registrationId + "_" + attributes.get("sub");
            email = attributes.get("email").toString();
            nickname = attributes.get("name").toString();

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        // 데이터베이스 조회 -> 존재하면 업데이트, 없으면 신규 가입
        Optional<UserEntity> entity = memberRepository.findByEmailAndIsSocial(username, true);
        if (entity.isPresent()) {
            // role 조회
            role = entity.get().getRoleType().name();

            // 기존 유저 업데이트
            UserRequestDTO dto = new UserRequestDTO();
            dto.setNickname(nickname);
            dto.setEmail(email);
            entity.get().updateUser(dto);

            memberRepository.save(entity.get());
        } else {
            // 신규 유저 추가
            UserEntity newUserEntity = UserEntity.builder()
                    .username(username)
                    .password("")
                    .isLock(false)
                    .isSocial(true)
                    .socialProviderType(SocialProviderType.valueOf(registrationId))
                    .roleType(UserRoleType.USER)
                    .nickname(nickname)
                    .email(email)
                    .build();

            memberRepository.save(newUserEntity);
        }

        authorities = List.of(new SimpleGrantedAuthority(role));

        return new CustomOAuth2User(attributes, authorities, username);
    }
}
