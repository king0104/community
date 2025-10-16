package kr.adapterz.community.domain.image.service;

import kr.adapterz.community.domain.image.dto.ImageUploadResponse;
import kr.adapterz.community.domain.image.entity.Image;
import kr.adapterz.community.domain.image.entity.ImageStatus;
import kr.adapterz.community.domain.image.repository.ImageRepository;
import kr.adapterz.community.external.s3.service.S3Service;
import kr.adapterz.community.global.exception.ErrorCode;
import kr.adapterz.community.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    /**
     * 이미지 업로드
     */
    @Transactional
    public ImageUploadResponse uploadImage(MultipartFile file) {
        // S3에 업로드
        String s3Url = s3Service.uploadFile(file, "profiles");

        // S3 키 추출
        String s3Key = s3Url.substring(s3Url.indexOf(".com/") + 5);

        // DB에 저장
        Image image = Image.createUploadCompletedImage(
                file.getOriginalFilename(),
                s3Key,
                s3Url,
                file.getSize(),
                file.getContentType()
        );

        Image savedImage = imageRepository.save(image);

        return ImageUploadResponse.of(savedImage);
    }

    /**
     * 이미지 조회
     */
    public Image getImage(Integer imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }

    /**
     * 이미지 삭제
     */
    @Transactional
    public void deleteImage(Integer imageId) {
        Image image = getImage(imageId);

        // S3에서 삭제
        s3Service.deleteFile(image.getS3Url());

        // DB에서 삭제
        imageRepository.delete(image);

    }
}
