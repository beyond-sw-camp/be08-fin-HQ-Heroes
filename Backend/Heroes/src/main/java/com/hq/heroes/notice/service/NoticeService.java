package com.hq.heroes.notice.service;

import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;
import com.hq.heroes.notice.entity.Notice;
import com.hq.heroes.notice.entity.enums.NoticeCategory;

import java.util.List;

public interface NoticeService {

    List<Notice> getNotices(NoticeCategory category);

    Notice getNoticeById(Long noticeId);

    Notice createNotice(NoticeRequestDTO requestDTO);

    Notice updateNotice(Long noticeId, NoticeUpdateRequestDTO requestDTO);

    boolean deleteNotice(Long noticeId);
}
