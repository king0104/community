package kr.adapterz.community.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 회원의 ID를 파라미터에 주입
 *
 * 사용 예시:
 * public void method(@LoginMember Integer memberId) { ... }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginMember {
    boolean required() default true;  // false면 로그인 안 해도 됨 (null 가능)
}