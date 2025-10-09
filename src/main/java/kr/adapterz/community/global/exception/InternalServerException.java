package kr.adapterz.community.global.exception;

public class InternalServerException extends CustomException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
