package com.hq.heroes.notice.service;

import com.hq.heroes.notice.dto.NoticeCategoryDTO;

import java.util.List;

public interface NoticeCategoryService {

    // 모든 공지사항 카테고리 조회
    List<NoticeCategoryDTO> getAllCategories();
}
