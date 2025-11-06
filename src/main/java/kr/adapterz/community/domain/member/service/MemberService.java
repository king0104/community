package kr.adapterz.community.domain.member.service;

import kr.adapterz.community.auth.refresh.repository.RefreshRepository;
import kr.adapterz.community.domain.image.entity.Image;
import kr.adapterz.community.domain.image.repository.ImageRepository;
import kr.adapterz.community.domain.member.dto.JoinRequest;
import kr.adapterz.community.domain.member.dto.MemberGetResponse;
import kr.adapterz.community.domain.member.dto.MemberJoinResponse;
import kr.adapterz.community.domain.member.dto.MemberPatchRequest;
import kr.adapterz.community.domain.member.dto.MemberPatchResponse;
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
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final RefreshRepository refreshRepository;
    private final PasswordEncoder passwordEncoder;

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

        return MemberJoinResponse.from(savedMember);
    }

    /**
     * 이메일로 회원 조회
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 회원 ID로 회원 정보 조회
     */
    public MemberGetResponse getMemberById(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberGetResponse.of(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getImage().getId()
        );
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public MemberPatchResponse patchMember(Integer memberId, MemberPatchRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 닉네임 변경
        if (request.getNickname() != null) {
            member.updateNickname(request.getNickname());
        }

        // 이미지 변경
        if (request.getProfileImageId() != null) {
            Image newImage = imageRepository.findById(request.getProfileImageId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));
            member.updateImage(newImage);
        }

        return MemberPatchResponse.from(member);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.isWithdrawn()) {
            throw new BadRequestException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        member.withdraw();
        refreshRepository.deleteByEmail(member.getEmail());
    }
}