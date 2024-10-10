package com.hq.heroes.notice.repository;

import com.hq.heroes.notice.entity.NoticeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeCategoryRepository extends JpaRepository<NoticeCategory, Long> {
    List<NoticeCategory> findAll();
}
