package kr.adapterz.community.auth.refresh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh_entity")
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String refresh;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    protected RefreshEntity () {

    }
    
    private RefreshEntity(
            String username,
            String refresh
    ) {
        this.username = username;
        this.refresh = refresh;
    }


    public static RefreshEntity createRefreshEntity(
            String username,
            String refresh
    ) {
        return new RefreshEntity(
                username,
                refresh
        );
    }




}
