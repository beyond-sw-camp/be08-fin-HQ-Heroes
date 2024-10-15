package com.hq.heroes.education.controller;

import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.repository.EducationCategoryRepository;
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
@RequestMapping("/api/v1/educationCategory-service")
@Tag(name = "EducationCategory APIs", description = "교육 카테고리 관련 API 목록")
public class EducationCategoryController {

    private final EducationCategoryRepository educationCategoryRepository;

    @GetMapping("/categories")
    @Operation(summary = "모든 카테고리 조회", description = "모든 카테고리의 목록을 조회한다.")
    public ResponseEntity<List<EducationCategory>> getAllCategories() {

        List<EducationCategory> categories = educationCategoryRepository.findAll();

        return ResponseEntity.ok(categories);
    }
}
