package kr.adapterz.community.global.exception;


public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(
            ErrorCode errorCode
    ) {
        this.errorCode = errorCode;
    }

}
