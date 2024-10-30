package com.hq.heroes.notice.repository;

import com.hq.heroes.notice.entity.Notice;
import com.hq.heroes.notice.entity.NoticeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByCategory(NoticeCategory category);

    List<Notice> findAllByOrderByCreatedAtDesc();

}
