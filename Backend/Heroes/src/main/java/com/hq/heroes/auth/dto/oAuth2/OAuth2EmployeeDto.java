package com.hq.heroes.auth.dto.oAuth2;

import lombok.Builder;
import lombok.Data;

@Data
public class OAuth2EmployeeDto {
    private String username;
    private String name;
    private String email;
    private String role;

    @Builder
    public OAuth2EmployeeDto(String username, String name, String email, String role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
