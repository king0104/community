package kr.adapterz.community.domain.image.dto;

import kr.adapterz.community.domain.image.entity.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ImageUploadResponse {
    private Integer imageId;
    private String imageUrl;
    private String fileName;
    private Long fileSize;

    public static ImageUploadResponse of(Image image) {
        return ImageUploadResponse.builder()
                .imageId(image.getId())
                .imageUrl(image.getS3Url())
                .fileName(image.getFileName())
                .fileSize(image.getFileSize())
                .build();
    }
}
