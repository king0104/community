package kr.adapterz.community.domain.image.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetadataRequest {

    @NotBlank(message = "파일명은 필수입니다")
    private String fileName;

    @NotBlank(message = "S3 키는 필수입니다")
    private String s3Key;

    @NotBlank(message = "S3 URL은 필수입니다")
    private String s3Url;

    @NotNull(message = "파일 크기는 필수입니다")
    private Long fileSize;

    @NotBlank(message = "콘텐츠 타입은 필수입니다")
    private String contentType;
}