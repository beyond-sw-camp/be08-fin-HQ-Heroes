package com.hq.heroes.auth.dto.oAuth2;

public interface OAuth2Response {
    public String getProvider();

    public String getProviderId();

    public String getName();

    public String getEmail();
}
