package kr.adapterz.community.global.exception;

import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400
    MEMBER_ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "이미 탈퇴한 회원입니다"),
    REFRESH_TOKEN_MISSED(HttpStatus.BAD_REQUEST, "해당 요청에 refresh token이 없습니다"),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않는 refresh token 입니다"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 refresh token 입니다"),
    FILE_MISSED(HttpStatus.BAD_REQUEST, "파일이 비어있습니다"),
    IMAGE_FILE_ONLY(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드 가능합니다"),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "파일은 최대 5MB까지 업로드 가능합니다"),
    UNSUPPORTED_FILE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다"),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "중복 이메일입니다"),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복 닉네임입니다"),

    // 401
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    //404
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 멤버를 찾을 수 없습니다"),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 이미지를 찾을 수 없습니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 게시글을 찾을 수 없습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 댓글을 찾을 수 없습니다"),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 부모 댓글을 찾을 수 없습니다"),

    // 500
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3에 파일 업로드 실패하였습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"예상치 못한 서버 에러가 발생했습니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
