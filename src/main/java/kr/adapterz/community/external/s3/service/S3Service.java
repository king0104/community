//package kr.adapterz.community.external.s3.service;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import jakarta.validation.Valid;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//import kr.adapterz.community.global.exception.BadRequestException;
//import kr.adapterz.community.global.exception.ErrorCode;
//import kr.adapterz.community.global.exception.InternalServerException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class S3Service {
//
//    private final AmazonS3 amazonS3;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    /**
//     * S3에 파일 업로드
//     */
//    public String uploadFile(MultipartFile file, String dirName) {
//        validateFile(file);
//
//        String fileName = createFileName(file.getOriginalFilename(), dirName);
//
//        try {
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType(file.getContentType());
//            metadata.setContentLength(file.getSize());
//            metadata.setCacheControl("max-age=31536000"); // 1년 캐싱
//
//            amazonS3.putObject(new PutObjectRequest(
//                    bucket,
//                    fileName,
//                    file.getInputStream(),
//                    metadata
//            ).withCannedAcl(CannedAccessControlList.PublicRead));
//
//            return amazonS3.getUrl(bucket, fileName).toString();
//
//        } catch (IOException e) {
//            throw new InternalServerException(ErrorCode.S3_UPLOAD_FAILED);
//        }
//    }
//
//    /**
//     * S3에서 파일 삭제
//     */
//    public void deleteFile(String s3Key) {
//        try {
//            if (amazonS3.doesObjectExist(bucket, s3Key)) {
//                amazonS3.deleteObject(bucket, s3Key);
//                log.info("S3 파일 삭제 성공: key={}", s3Key);
//            }
//        } catch (Exception e) {
//            // 삭제 실패해도 게시글 삭제 등 다른 로직은 그대로 진행
//            log.warn("S3 파일 삭제 실패: key={}, error={}", s3Key, e.getMessage());
//        }
//    }
//
//    /**
//     * 파일명 생성 (중복 방지)
//     */
//    private String createFileName(String originalFilename, String dirName) {
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        String uuid = UUID.randomUUID().toString().substring(0, 8);
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//
//        return String.format("%s/%s_%s%s", dirName, timestamp, uuid, extension);
//    }
//
//    /**
//     * 파일 유효성 검증
//     */
//    private void validateFile(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new BadRequestException(ErrorCode.FILE_MISSED);
//        }
//
//        // 이미지 파일 확인
//        String contentType = file.getContentType();
//        if (contentType == null || !contentType.startsWith("image/")) {
//            throw new BadRequestException(ErrorCode.IMAGE_FILE_ONLY);
//        }
//
//        // 파일 크기 확인 (5MB)
//        long maxSize = 5 * 1024 * 1024;
//        if (file.getSize() > maxSize) {
//            throw new BadRequestException(ErrorCode.FILE_SIZE_EXCEED);
//        }
//
//        // 허용된 확장자 확인
//        String filename = file.getOriginalFilename();
//        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
//        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
//
//        if (!allowedExtensions.contains(extension)) {
//            throw new BadRequestException(ErrorCode.UNSUPPORTED_FILE_FORMAT);
//        }
//    }
//}
