package com.hq.heroes.common.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthCodeReqDTO {

    private String email;    // 요청 이메일
    private String authCode;    // 인증코드
    private String expiration;

}
