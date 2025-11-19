package kr.adapterz.community.domain.image.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.domain.image.dto.ImageMetadataRequest;
import kr.adapterz.community.domain.image.dto.ImageUploadResponse;
import kr.adapterz.community.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

//    /**
//     * 기존 업로드 방식 (Deprecated - Lambda로 이관 예정)
//     */
//    @PostMapping
//    public ResponseEntity<ImageUploadResponse> uploadImage(
//            @RequestParam("file") MultipartFile file) {
//
//        ImageUploadResponse response = imageService.uploadImage(file);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(response);
//    }

    /**
     * Lambda에서 S3 업로드 완료 후 메타데이터 저장을 위해 호출하는 엔드포인트
     */
    @PostMapping("/metadata")
    public ResponseEntity<ImageUploadResponse> saveImageMetadata(
            @Valid @RequestBody ImageMetadataRequest request) {

        ImageUploadResponse response = imageService.saveImageMetadata(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


//    @DeleteMapping("/{imageId}")
//    public ResponseEntity<Void> deleteImage(
//            @PathVariable Integer imageId) {
//
//        imageService.deleteImage(imageId);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .build();
//    }
}