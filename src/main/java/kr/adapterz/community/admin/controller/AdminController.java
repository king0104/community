package kr.adapterz.community.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    // TODO: 정상적인 admin 로직 작성 (현재 테스트용 api)
    @GetMapping
    public ResponseEntity<String> admin() {
        return ResponseEntity.status(HttpStatus.OK)
                .body("admin controller");
    }
}

