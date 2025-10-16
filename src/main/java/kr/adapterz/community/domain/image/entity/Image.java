package kr.adapterz.community.domain.image.entity;

import jakarta.persistence.*;
import kr.adapterz.community.common.BaseEntity;
import kr.adapterz.community.domain.post.entity.Post;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String s3Url;

    private Long fileSize;

    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageStatus status;

    // 업로드 완료 시 Image 생성
    public static Image createUploadCompletedImage(String fileName, String s3Key, String s3Url,
                           Long fileSize, String mimeType) {
        return Image.builder()
                .fileName(fileName)
                .s3Key(s3Key)
                .s3Url(s3Url)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .status(ImageStatus.COMPLETED)
                .build();
    }



}
