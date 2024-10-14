package com.hq.heroes.auth.controller;

import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원가입 APIs", description = "회원가입 서비스")
public class SignController {

    private final JoinService joinService;

    @PostMapping("/join")
    @ResponseBody
    @Operation(summary = "회원 가입", description = "회원 가입 기능")
    public ResponseEntity<?> join(@Validated @ModelAttribute JoinDTO joinDto) {
        Map<String, Object> response = new HashMap<>();
        MultipartFile profileImage = joinDto.getProfileImage();

        // 프로필 이미지 파일 저장 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            // 이미지 파일 MIME 타입 검증
            if (!profileImage.getContentType().startsWith("image/")) {
                response.put("success", false);
                response.put("message", "이미지 파일만 업로드할 수 있습니다.");
                return ResponseEntity.status(400).body(response);
            }

            joinDto.setProfileImage(profileImage); // 이미지 파일 설정
        }

        try {
            // 회원 가입 처리
            String joinResult = joinService.join(joinDto);
            response.put("success", true);
            response.put("username", joinDto.getEmployeeName());
            response.put("joinResult", joinResult); // 회원 가입 결과 추가

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원 가입 실패: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}