package com.hq.heroes.notice.service;

import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;
import com.hq.heroes.notice.entity.Notice;

import java.util.List;

public interface NoticeService {

    List<Notice> getNotices();

    // 없애라
    List<Notice> getNoticesByCategory(Long categoryId);

    //- 테스트
    Notice getNoticeById(Long noticeId);

    //- 테스트
    Notice createNotice(NoticeRequestDTO requestDTO);

    //- 테스트
    Notice updateNotice(Long noticeId, NoticeUpdateRequestDTO requestDTO);

    //- 테스트
    boolean deleteNotice(Long noticeId);
}
