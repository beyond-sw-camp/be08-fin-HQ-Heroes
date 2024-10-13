package com.hq.heroes.notification.service;

import com.hq.heroes.notification.dto.NotificationCategoryReqDTO;
import com.hq.heroes.notification.entity.NotificationCategory;
import com.hq.heroes.notification.repository.NotificationCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationCategoryServiceImpl implements NotificationCategoryService {

    private final NotificationCategoryRepository notificationCategoryRepository;

    @Override
    public List<NotificationCategory> getAllCategories() {
        return notificationCategoryRepository.findAll();
    }

    @Override
    public NotificationCategory getCategoryById(Long categoryId) {
        return notificationCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
    }

    @Override
    @Transactional
    public NotificationCategory createCategory(NotificationCategoryReqDTO requestDTO) {
        NotificationCategory category = NotificationCategory.builder()
                .notificationCategoryName(requestDTO.getCategoryName())
                .build();

        return notificationCategoryRepository.save(category);
    }

    @Override
    @Transactional
    public NotificationCategory updateCategory(Long categoryId, NotificationCategoryReqDTO requestDTO) {
        NotificationCategory category = notificationCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found for ID: " + categoryId));

        category.setNotificationCategoryName(requestDTO.getCategoryName());
        return notificationCategoryRepository.save(category);
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        if (notificationCategoryRepository.existsById(categoryId)) {
            notificationCategoryRepository.deleteById(categoryId);
            return true;
        }
        return false;
    }
}
