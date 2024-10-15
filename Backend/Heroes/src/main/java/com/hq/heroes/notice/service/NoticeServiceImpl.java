package com.hq.heroes.notice.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;
import com.hq.heroes.notice.entity.Notice;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notice.entity.NoticeCategory;
import com.hq.heroes.notice.repository.NoticeCategoryRepository;
import com.hq.heroes.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCategoryRepository noticeCategoryRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<Notice> getNotices() {
        return noticeRepository.findAll();
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
    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElse(null);
    }

    @Override
    @Transactional
    public Notice createNotice(NoticeRequestDTO requestDTO) {
        // 사원 정보를 가져옴
        Optional<Employee> employee = employeeRepository.findById(requestDTO.getEmployeeId());
        if (employee.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee ID");
        }

        System.out.println("requestDTO.categoryId = " + requestDTO.getCategoryId());


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

        return noticeRepository.save(notice);
    }

    @Override
    @Transactional
    public Notice updateNotice(Long noticeId, NoticeUpdateRequestDTO requestDTO) {
        // 공지사항 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항 ID : " + noticeId));

        // 공지사항 정보 업데이트
        notice.setTitle(requestDTO.getTitle());
        notice.setContent(requestDTO.getContent());
        notice.setUpdater(requestDTO.getUpdater());
        notice.setCategory(requestDTO.getCategory());

        return noticeRepository.save(notice);
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
