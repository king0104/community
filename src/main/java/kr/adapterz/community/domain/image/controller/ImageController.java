package kr.adapterz.community.domain.image.controller;

import kr.adapterz.community.domain.image.dto.ImageUploadResponse;
import kr.adapterz.community.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file) {

        ImageUploadResponse response = imageService.uploadImage(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Integer imageId) {

        imageService.deleteImage(imageId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}