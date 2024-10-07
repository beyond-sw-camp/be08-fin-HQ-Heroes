package com.hq.heroes.notice.repository;

import com.hq.heroes.notice.entity.NoticeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeCategoryReposiroty extends JpaRepository<NoticeCategory, Long> {

}
