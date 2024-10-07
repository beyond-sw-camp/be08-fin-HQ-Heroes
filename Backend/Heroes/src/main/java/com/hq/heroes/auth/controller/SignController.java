package com.hq.heroes.auth.controller;

import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.service.JoinService;
import com.hq.heroes.common.config.UploadProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원가입 APIs", description = "회원가입 서비스")
public class SignController {

    private final JoinService joinService;

    @Autowired
    private UploadProperties uploadProperties; // UploadProperties 주입

    @PostMapping("/join")
    @ResponseBody
    @Operation(summary = "회원 가입", description = "회원 가입 기능")
    public ResponseEntity<?> join(@Validated @ModelAttribute JoinDTO joinDto,
                                  @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Map<String, Object> response = new HashMap<>();

        // 프로필 이미지 파일 저장 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            String originalFileName = profileImage.getOriginalFilename();
            String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            String shortUuid = UUID.randomUUID().toString().substring(0, 8);
            String uuidFileName = shortUuid + fileExtension;
            Path filePath = Paths.get(uploadProperties.getDir(), uuidFileName);

            try {
                Files.createDirectories(filePath.getParent()); // 디렉토리 생성
                profileImage.transferTo(filePath.toFile()); // 파일 저장

                // 프로필 이미지의 URL을 저장 (서버의 정적 파일 경로 제공)
                String profileImageUrl = "/files/" + uuidFileName;
                joinDto.setProfileImageUrl(profileImageUrl); // 이미지 URL 설정
            } catch (IOException e) {
                response.put("success", false);
                response.put("message", "파일 업로드 실패: " + e.getMessage());
                return ResponseEntity.status(400).body(response);
            }
        }

        try {
            // 회원 가입 처리
            String joinResult = joinService.join(joinDto);
            response.put("success", true);
            response.put("username", joinDto.getEmployeeName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원 가입 실패: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }


}
