package com.hq.heroes.auth.dto.oAuth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@RequiredArgsConstructor
public class CustomOAuth2Employee implements OAuth2User {
    private final OAuth2EmployeeDto oAuth2EmployeeDto;

    // 통일 x -> return null
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return oAuth2EmployeeDto.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return oAuth2EmployeeDto.getName();
    }

    public String getUsername() {
        return oAuth2EmployeeDto.getUsername();
    }

    public String getEmail() {
        return oAuth2EmployeeDto.getEmail();
    }
}
