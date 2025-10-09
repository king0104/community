package kr.adapterz.community.global.dto;

import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final int status;
    private final String message;

    private ExceptionResponse(
            int status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    public static ExceptionResponse fail(
            int status,
            String message
    ) {
        return new ExceptionResponse(
                status,
                message
        );
    }
}
