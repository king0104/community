package kr.adapterz.community.auth.refresh.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String refresh;
    private String expiration;

    protected RefreshEntity () {

    }
    
    private RefreshEntity(
            String username,
            String refresh,
            String expiration
    ) {
        this.username = username;
        this.refresh = refresh;
        this.expiration = expiration;
    }


    public static RefreshEntity createRefreshEntity(
            String username,
            String refresh,
            String expiration
    ) {
        return new RefreshEntity(
                username,
                refresh,
                expiration
        );
    }




}
