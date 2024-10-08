package com.hq.heroes.notice.controller;

import com.hq.heroes.notice.dto.NoticeCategoryDTO;
import com.hq.heroes.notice.service.NoticeCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category-service")
@Tag(name = "NoticeCategory APIs", description = "공지사항 카테고리 관련 API 목록")
public class NoticeCategoryController {

    private final NoticeCategoryService noticeCategoryService;

    @GetMapping("/categories")
    @Operation(summary = "모든 카테고리 조회", description = "모든 카테고리의 목록을 조회한다.")
    public ResponseEntity<List<NoticeCategoryDTO>> getAllCategories() {
        List<NoticeCategoryDTO> categories = noticeCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
