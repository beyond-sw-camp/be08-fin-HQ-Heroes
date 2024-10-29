package com.hq.heroes.notification.service;

import com.hq.heroes.notification.dto.NotificationCategoryReqDTO;
import com.hq.heroes.notification.entity.NotificationCategory;

import java.util.List;

public interface NotificationCategoryService {
    List<NotificationCategory> getAllCategories();

    NotificationCategory getCategoryById(Long categoryId);

    NotificationCategory createCategory(NotificationCategoryReqDTO requestDTO);

    NotificationCategory updateCategory(Long categoryId, NotificationCategoryReqDTO requestDTO);

    boolean deleteCategory(Long categoryId);
}