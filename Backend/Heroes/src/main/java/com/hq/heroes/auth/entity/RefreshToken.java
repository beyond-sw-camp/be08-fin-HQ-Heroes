package com.hq.heroes.auth.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    private String employeeId;
    private String refreshToken;
    private Long expiration;

    public RefreshToken(String employeeId, String refreshToken, Long expiration) {
        this.employeeId = employeeId;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
