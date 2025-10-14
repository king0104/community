package kr.adapterz.community.auth.jwt;

public class JwtConstants {
    // 토큰 유효 시간
    public static final Long ACCESS_TOKEN_EXPIRATION = 600_000L; // 10분
    public static final Long REFRESH_TOKEN_EXPIRATION = 86_400_000L; // 24시간

    // 쿠키 설정
    public static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 24시간 (초 단위)
    public static final String COOKIE_NAME_REFRESH = "refresh";
    public static final String HEADER_NAME_ACCESS = "access";

    // 토큰 카테고리
    public static final String TOKEN_CATEGORY_ACCESS = "access";
    public static final String TOKEN_CATEGORY_REFRESH = "refresh";
}
