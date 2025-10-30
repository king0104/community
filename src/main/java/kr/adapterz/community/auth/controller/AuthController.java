package kr.adapterz.community.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.adapterz.community.auth.dto.LoginRequest;
import kr.adapterz.community.auth.dto.LoginResponse;
import kr.adapterz.community.auth.service.AuthService;
import kr.adapterz.community.auth.session.SessionDto;
import kr.adapterz.community.domain.member.entity.Member;
import kr.adapterz.community.domain.member.repository.MemberRepository;
import kr.adapterz.community.global.exception.CustomException;
import kr.adapterz.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        // 로그인 확인
        Member member = authService.login(request);

        // 세션 생성
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("loginMember", SessionDto.of(
                member.getId(),
                member.getRole()
        ));

        // 응답 반환 - 이때 set-cookie 헤더 자동으로 추가
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LoginResponse.from(member));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}