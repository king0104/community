package kr.adapterz.community.auth.controller;

import kr.adapterz.community.auth.dto.LoginRequest;
import kr.adapterz.community.auth.dto.LoginResponse;
import kr.adapterz.community.auth.service.AuthService;
import kr.adapterz.community.global.dto.DefaultResponse;
import kr.adapterz.community.global.exception.BadRequestException;
import kr.adapterz.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException(ErrorCode.REFRESH_TOKEN_MISSED);
        }

        LoginResponse response = authService.reissue(refreshToken);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultResponse> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException(ErrorCode.INVALID_TOKEN);
        }

        String email = authentication.getName();
        authService.logout(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DefaultResponse.of("로그아웃 성공"));
    }
}
