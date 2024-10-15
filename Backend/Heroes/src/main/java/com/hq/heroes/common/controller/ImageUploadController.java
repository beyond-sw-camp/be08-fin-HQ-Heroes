package com.hq.heroes.common.controller;

import com.hq.heroes.common.service.FirebaseStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ImageUploadController {

    private final FirebaseStorageService firebaseStorageService;

    public ImageUploadController(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 파일이 비어있는지 체크
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "파일이 없습니다.");
                return ResponseEntity.status(400).body(response);
            }

            // 파일 MIME 타입 검증 (이미지 파일만 허용)
            if (!file.getContentType().startsWith("image/")) {
                response.put("success", false);
                response.put("message", "이미지 파일만 업로드할 수 있습니다.");
                return ResponseEntity.status(400).body(response);
            }

            // 랜덤 UUID를 이용한 파일명 생성
            String randomFileName = UUID.randomUUID().toString() + "_profile.png";

            // Firebase Storage에 파일 업로드
            String imageUrl = firebaseStorageService.uploadFile(file, randomFileName);

            response.put("success", true);
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
