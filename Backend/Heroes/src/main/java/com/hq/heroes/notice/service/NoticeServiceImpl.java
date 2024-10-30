package com.hq.heroes.notice.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeResponseDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;
import com.hq.heroes.notice.entity.Notice;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notice.entity.NoticeCategory;
import com.hq.heroes.notice.repository.NoticeCategoryRepository;
import com.hq.heroes.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCategoryRepository noticeCategoryRepository;
    private final EmployeeRepository employeeRepository;

    private NoticeResponseDTO convertToResponseDTO(Notice notice) {

        return NoticeResponseDTO.builder()
                .noticeId(notice.getNoticeId())
                .employeeId(notice.getEmployee().getEmployeeId())
                .employeeName(notice.getEmployee().getEmployeeName())
                .createdAt(notice.getCreatedAt())
                .updateAt(notice.getUpdateAt())
                .updaterId(notice.getUpdater() != null ? notice.getUpdater().getEmployeeId() : null)
                .updaterName(notice.getUpdater() != null ? notice.getUpdater().getEmployeeName() : null)
                .title(notice.getTitle())
                .content(notice.getContent())
                .categoryId(notice.getCategory().getNoticeCategoryId())
                .categoryName(notice.getCategory().getCategoryName())
                .build();
    }


    @Override
    public List<NoticeResponseDTO> getNotices() {
        // 공지사항 엔티티 리스트 조회
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc();

        // 엔티티 리스트를 DTO 리스트로 변환
        return notices.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notice> getNoticesByCategory(Long categoryId) {
        Optional<NoticeCategory> category = noticeCategoryRepository.findById(categoryId);

        if (category.isPresent()) {
            return noticeRepository.findByCategory(category.get());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public NoticeResponseDTO getNoticeById(Long noticeId) {
        // 공지사항 엔티티를 ID로 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElse(null);

        // 엔티티가 null이 아니면 DTO로 변환하여 반환
        if (notice != null) {
            return convertToResponseDTO(notice);
        }
        return null; // null일 경우
    }

    @Override
    @Transactional
    public NoticeResponseDTO createNotice(NoticeRequestDTO requestDTO) {
        // 사원 정보를 가져옴
        Optional<Employee> employee = employeeRepository.findById(requestDTO.getEmployeeId());
        if (employee.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee ID");
        }

        Employee employeeEntity = employee.get();

        // 카테고리 정보를 가져옴
        Optional<NoticeCategory> category = noticeCategoryRepository.findById(requestDTO.getCategoryId());
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Invalid category ID");
        }

        // 공지사항 생성
        Notice notice = Notice.builder()
                .employee(employeeEntity)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .updater(null)
                .category(category.get()) // 조회된 카테고리 설정
                .build();

        noticeRepository.save(notice);

        return convertToResponseDTO(notice);
    }

    @Override
    @Transactional
    public NoticeResponseDTO updateNotice(Long noticeId, NoticeUpdateRequestDTO requestDTO) {
        // 공지사항 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항 ID : " + noticeId));

        Hibernate.initialize(notice.getEmployee()); // employee 초기화
        Hibernate.initialize(notice.getUpdater());  // updater 초기화

        Employee employee = employeeRepository.findByEmployeeId(requestDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("사원이 존재하지 않습니다"));

        NoticeCategory category = noticeCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID : " + requestDTO.getCategoryId()));

        // 공지사항 업데이트
        notice.updateNotice(requestDTO, employee, noticeCategoryRepository);

        // 엔티티를 저장하고 DTO로 변환
        noticeRepository.save(notice);

        // DTO 변환
        return convertToResponseDTO(notice);
    }


    @Override
    @Transactional
    public boolean deleteNotice(Long noticeId) {
        // 공지사항이 존재하는지 확인
        if (noticeRepository.existsById(noticeId)) {
            noticeRepository.deleteById(noticeId);
            return true;
        }
        return false;
    }
}
