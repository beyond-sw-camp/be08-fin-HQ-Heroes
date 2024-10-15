package com.hq.heroes.common.controller;

import com.hq.heroes.common.dto.EmailMessage;
import com.hq.heroes.common.dto.EmailReqDTO;
import com.hq.heroes.common.dto.EmailResDTO;
import com.hq.heroes.common.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mails")
@Tag(name = "메일 서비스 APIs", description = "메일 서비스 Api 리스트")
public class EmailController {

    private final EmailService emailService;

    // 임시 비밀번호 발급
    @PostMapping("/password")
    @Operation(summary = "임시 비밀번호 발급", description = "임시 비밀번호를 발급하여 메일로 전송한다.")
    public ResponseEntity<Void> sendPasswordMail(@RequestBody EmailReqDTO emailReqDTO) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailReqDTO.getEmail())
                .subject("[HQ-HeRoes] 임시 비밀번호 발급")
                .build();

        emailService.sendMail(emailMessage, "/mails/changePassword");

        return ResponseEntity.ok().build();
    }

    // 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
    @PostMapping("/email")
    @Operation(summary = "인증 코드 발급", description = "인증 코드를 발급하여 메일로 전송한다.")
    public ResponseEntity<Void> sendAuthCodeMail(@RequestBody EmailReqDTO emailReqDTO) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailReqDTO.getEmail())
                .subject("[HQ-HeRoes]이메일 인증을 위한 인증 코드 발송")
                .build();

        emailService.sendMail(emailMessage, "/mails/authCode");

        return ResponseEntity.ok().build();
    }

}
