package com.hq.heroes.notice.service;

import com.hq.heroes.notice.dto.NoticeCategoryDTO;
import com.hq.heroes.notice.entity.NoticeCategory;
import com.hq.heroes.notice.repository.NoticeCategoryReposiroty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeCategoryServiceImpl implements NoticeCategoryService {
    private final NoticeCategoryReposiroty noticeCategoryReposiroty;

    @Override
    public List<NoticeCategoryDTO> getAllCategories() {
        List<NoticeCategory> categories = noticeCategoryReposiroty.findAll();
        return categories.stream()
                .map(category -> {
                    NoticeCategoryDTO categoryDTO = new NoticeCategoryDTO();
                    categoryDTO.setId(category.getNoticeCategoryId());
                    categoryDTO.setName(category.getCategoryName());
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }
}
