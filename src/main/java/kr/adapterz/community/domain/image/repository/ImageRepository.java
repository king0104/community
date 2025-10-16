package kr.adapterz.community.domain.image.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.adapterz.community.domain.image.entity.Image;
import kr.adapterz.community.domain.image.entity.ImageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByStatusAndCreatedAtBefore(ImageStatus status, LocalDateTime dateTime);



}
