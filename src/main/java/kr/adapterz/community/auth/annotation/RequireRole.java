package kr.adapterz.community.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 권한이 필요한 API에 사용
 *
 * 사용 예시:
 * @RequireRole("ROLE_ADMIN")
 * public void adminOnly() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();  // {"ROLE_ADMIN", "ROLE_USER"}
}