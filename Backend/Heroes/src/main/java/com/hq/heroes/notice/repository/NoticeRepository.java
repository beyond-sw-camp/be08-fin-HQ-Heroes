package com.hq.heroes.notice.repository;

import com.hq.heroes.notice.entity.Notice;
import com.hq.heroes.notice.entity.NoticeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 카테고리로 공지사항 조회
    List<Notice> findByCategory(NoticeCategory category);

    // 생성된 순서 내림차순으로 공지사항 조회
    List<Notice> findAllByOrderByCreatedAtDesc();

}
