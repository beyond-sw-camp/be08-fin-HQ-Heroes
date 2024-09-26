package com.hq.heroes.notice.repository;

import com.hq.heroes.notice.entity.Notice;
import com.hq.heroes.notice.entity.enums.NoticeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeReposiroty extends JpaRepository<Notice, Long> {
    List<Notice> findByCategory(NoticeCategory category);

}
