package kr.adapterz.community.global.exception;

public class UnAuthorizedException extends CustomException {

    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
