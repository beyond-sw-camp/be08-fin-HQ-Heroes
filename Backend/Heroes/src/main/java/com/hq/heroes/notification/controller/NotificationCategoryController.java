package com.hq.heroes.notification.controller;

import com.hq.heroes.notification.dto.NotificationCategoryReqDTO;
import com.hq.heroes.notification.dto.NotificationCategoryResDTO;
import com.hq.heroes.notification.entity.NotificationCategory;
import com.hq.heroes.notification.service.NotificationCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-service")
@Tag(name = "Notification Category APIs", description = "알림 카테고리 관련 API 목록")
public class NotificationCategoryController {

    private final NotificationCategoryService notificationCategoryService;

    @GetMapping("/categories")
    @Operation(summary = "알림 카테고리 목록 조회", description = "전체 알림 카테고리 목록을 조회한다.")
    public ResponseEntity<List<NotificationCategoryResDTO>> getAllCategories() {
        List<NotificationCategory> categories = notificationCategoryService.getAllCategories();
        List<NotificationCategoryResDTO> categoryDTOs = categories.stream()
                .map(category -> NotificationCategoryResDTO.builder()
                        .notificationCategoryId(category.getNotificationCategoryId())
                        .categoryName(category.getNotificationCategoryName())
                        .build())
                .collect(Collectors.toList());
        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }

    @GetMapping("/category/{category-id}")
    @Operation(summary = "알림 카테고리 상세 조회", description = "카테고리 ID로 해당 카테고리 정보를 조회한다.")
    public ResponseEntity<NotificationCategoryResDTO> getCategoryById(
            @Parameter(description = "카테고리 ID", example = "1") @PathVariable("category-id") Long categoryId) {
        NotificationCategory category = notificationCategoryService.getCategoryById(categoryId);

        NotificationCategoryResDTO categoryDTO = NotificationCategoryResDTO.builder()
                .notificationCategoryId(category.getNotificationCategoryId())
                .categoryName(category.getNotificationCategoryName())
                .build();

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @PostMapping("/category")
    @Operation(summary = "알림 카테고리 등록", description = "알림 카테고리 정보를 받아 등록한다.")
    public ResponseEntity<NotificationCategoryResDTO> createCategory(@RequestBody NotificationCategoryReqDTO requestDTO) {
        NotificationCategory category = notificationCategoryService.createCategory(requestDTO);
        NotificationCategoryResDTO categoryDTO = NotificationCategoryResDTO.builder()
                .notificationCategoryId(category.getNotificationCategoryId())
                .categoryName(category.getNotificationCategoryName())
                .build();

        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
    }

    @PutMapping("/category/{category-id}")
    @Operation(summary = "알림 카테고리 수정", description = "알림 카테고리 정보를 받아 수정한다.")
    public ResponseEntity<NotificationCategoryResDTO> updateCategory(
            @Parameter(description = "카테고리 ID", example = "1") @PathVariable("category-id") Long categoryId,
            @RequestBody NotificationCategoryReqDTO requestDTO) {
        NotificationCategory updatedCategory = notificationCategoryService.updateCategory(categoryId, requestDTO);
        NotificationCategoryResDTO categoryDTO = NotificationCategoryResDTO.builder()
                .notificationCategoryId(updatedCategory.getNotificationCategoryId())
                .categoryName(updatedCategory.getNotificationCategoryName())
                .build();

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @DeleteMapping("/category/{category-id}")
    @Operation(summary = "알림 카테고리 삭제", description = "카테고리 ID로 해당 알림 카테고리를 삭제한다.")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "카테고리 ID", example = "1") @PathVariable("category-id") Long categoryId) {
        boolean isDeleted = notificationCategoryService.deleteCategory(categoryId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
