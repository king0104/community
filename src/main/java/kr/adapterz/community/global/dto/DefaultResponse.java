package kr.adapterz.community.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultResponse {
    private String message;

    public static DefaultResponse of(String message) {
        return DefaultResponse.builder()
                .message(message)
                .build();
    }
}