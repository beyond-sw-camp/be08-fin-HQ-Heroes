package com.hq.heroes.notice.service;

import com.hq.heroes.notice.dto.NoticeCategoryDTO;

import java.util.List;

public interface NoticeCategoryService {
    List<NoticeCategoryDTO> getAllCategories();
}
