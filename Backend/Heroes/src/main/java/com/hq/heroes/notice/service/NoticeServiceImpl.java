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
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc();
        return notices.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NoticeResponseDTO getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElse(null);

        if (notice != null) {
            return convertToResponseDTO(notice);
        }
        return null;
    }

    @Override
    @Transactional
    public NoticeResponseDTO createNotice(NoticeRequestDTO requestDTO) {
        Optional<Employee> employee = employeeRepository.findById(requestDTO.getEmployeeId());
        if (employee.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee ID");
        }

        Employee employeeEntity = employee.get();

        Optional<NoticeCategory> category = noticeCategoryRepository.findById(requestDTO.getCategoryId());
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Invalid category ID");
        }

        Notice notice = Notice.builder()
                .employee(employeeEntity)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .updater(null)
                .category(category.get())
                .build();

        noticeRepository.save(notice);

        return convertToResponseDTO(notice);
    }

    @Override
    @Transactional
    public NoticeResponseDTO updateNotice(Long noticeId, NoticeUpdateRequestDTO requestDTO) {

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항 ID : " + noticeId));

        Hibernate.initialize(notice.getEmployee());
        Hibernate.initialize(notice.getUpdater());

        Employee employee = employeeRepository.findByEmployeeId(requestDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("사원이 존재하지 않습니다"));

        NoticeCategory category = noticeCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID : " + requestDTO.getCategoryId()));

        notice.updateNotice(requestDTO, employee, noticeCategoryRepository);

        noticeRepository.save(notice);

        return convertToResponseDTO(notice);
    }


    @Override
    @Transactional
    public boolean deleteNotice(Long noticeId) {

        if (noticeRepository.existsById(noticeId)) {
            noticeRepository.deleteById(noticeId);
            return true;
        }
        return false;
    }
}
