package kr.adapterz.community.auth.jwt;

/**
 * JWT 관련 상수
 */
public class JwtConstants {

    // 헤더 이름
    public static final String HEADER_NAME_ACCESS = "access";
    public static final String HEADER_NAME_REFRESH = "refresh";

    // 쿠키 이름
    public static final String COOKIE_NAME_REFRESH = "refresh";

    // 토큰 만료 시간
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;  // 30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;  // 7일

    private JwtConstants() {
        // 상수 클래스이므로 인스턴스 생성 방지
    }
}