package com.hq.heroes.auth.controller;

import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> join(@ModelAttribute JoinDTO joinDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            String joinResult = joinService.join(joinDto);
            response.put("success", true);
            response.put("username", joinDto.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}
