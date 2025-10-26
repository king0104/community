package kr.adapterz.community.page.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    /**
     * 이용약관 페이지
     * URL: http://localhost:8080/terms
     */
    @GetMapping("/terms")
    public String showTerms() {
        return "terms";
    }

    /**
     * 개인정보처리방침 페이지
     * URL: http://localhost:8080/privacy
     */
    @GetMapping("/privacy")
    public String showPrivacy() {
        return "privacy";
    }
}