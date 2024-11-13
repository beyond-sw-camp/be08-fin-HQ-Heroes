package com.hq.heroes.notice.service;

import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeResponseDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;

import java.util.List;

public interface NoticeService {

    // 공지사항 조회
    List<NoticeResponseDTO> getNotices();

    // 공지사항 번호로 공지사항 조회
    NoticeResponseDTO getNoticeById(Long noticeId);

    // 공지사항 생성
    NoticeResponseDTO createNotice(NoticeRequestDTO requestDTO);

    // 공지사항 수정
    NoticeResponseDTO updateNotice(Long noticeId, NoticeUpdateRequestDTO requestDTO);

    // 공지사항 삭제
    boolean deleteNotice(Long noticeId);
}
