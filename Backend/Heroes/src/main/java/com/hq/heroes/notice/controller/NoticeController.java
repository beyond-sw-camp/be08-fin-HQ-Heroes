package com.hq.heroes.notice.controller;

import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeResponseDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;
import com.hq.heroes.notice.entity.Notice;
import com.hq.heroes.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notice-service")
@Tag(name = "Notice APIs", description = "공지사항 관련 API 목록")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회
    @GetMapping("/notice")
    @Operation(summary = "공지사항 목록 조회", description = "전체 공지사항의 목록을 조회한다.")
    public ResponseEntity<List<NoticeResponseDTO>> getNotices() {
        List<NoticeResponseDTO> noticeDTOs = noticeService.getNotices(); // DTO 리스트 반환

        // 빈 리스트에 대해서도 200 OK 반환
        return new ResponseEntity<>(noticeDTOs, HttpStatus.OK);
    }


    // 특정 공지사항 조회
    @GetMapping("/notice/{notice-id}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 ID로 해당공지사항의 정보를 조회한다.")
    public ResponseEntity<NoticeResponseDTO> getNoticeById(
            @Parameter(description = "공지사항 ID", example = "1") @PathVariable("notice-id") Long noticeId) {

        NoticeResponseDTO noticeDTO = noticeService.getNoticeById(noticeId);

        if (noticeDTO != null) {
            return new ResponseEntity<>(noticeDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    // 공지사항 등록
    @PostMapping("/notice")
    @Operation(summary = "공지사항 등록", description = "공지사항 정보를 받아서 등록한다.")
    public ResponseEntity<NoticeResponseDTO> create(@RequestBody NoticeRequestDTO requestDTO) {

        NoticeResponseDTO responseDTO = noticeService.createNotice(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 공지사항 수정
    @PutMapping("/notice/{notice-id}")
    public ResponseEntity<NoticeResponseDTO> update(
            @Parameter(description = "공지사항 ID", example = "1") @PathVariable("notice-id") Long noticeId,
            @RequestBody NoticeUpdateRequestDTO requestDTO) {

        System.out.println("공지사항 수정 noticeId = " + noticeId);

        NoticeResponseDTO responseDTO = noticeService.updateNotice(noticeId, requestDTO);

        if (responseDTO != null) {
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 공지사항 삭제 - 테스트
    @DeleteMapping("/notice/{notice-id}")
    @Operation(summary = "공지사항 삭제", description = "공지사항 ID로 해당 공지사항을 삭제한다.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "공지사항 ID", example = "1") @PathVariable("notice-id") Long noticeId) {

        boolean isDeleted = noticeService.deleteNotice(noticeId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
