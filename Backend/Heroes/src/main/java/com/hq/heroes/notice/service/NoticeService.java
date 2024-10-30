package com.hq.heroes.notice.service;

import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeResponseDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;
import com.hq.heroes.notice.entity.Notice;

import java.util.List;

public interface NoticeService {

    List<NoticeResponseDTO> getNotices();

    List<Notice> getNoticesByCategory(Long categoryId);

    NoticeResponseDTO getNoticeById(Long noticeId);

    NoticeResponseDTO createNotice(NoticeRequestDTO requestDTO);

    NoticeResponseDTO updateNotice(Long noticeId, NoticeUpdateRequestDTO requestDTO);

    boolean deleteNotice(Long noticeId);
}
