package com.hq.heroes.auth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tb_refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;
    private String refresh;
    private String expiration;

    @Builder
    public RefreshToken(String employeeId, String refresh, String expiration) {
        this.employeeId = employeeId;
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
