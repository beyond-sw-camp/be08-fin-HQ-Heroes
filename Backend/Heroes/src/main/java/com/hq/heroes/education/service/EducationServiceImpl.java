package com.hq.heroes.education.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.entity.enums.CourseStatus;
import com.hq.heroes.education.repository.CourseRepository;
import com.hq.heroes.education.repository.EducationCategoryRepository;
import com.hq.heroes.education.repository.EducationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final EducationCategoryRepository educationCategoryRepository;
    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;
    private final CourseServiceImpl courseServiceImpl;

    @Override
    public List<Education> getEducations() {
            return educationRepository.findAll();
    }

    @Override
    public Education getEducationById(Long educationId) {
        return educationRepository.findById(educationId).orElse(null);
    }

//    @Override
//    public boolean incrementCurrentParticipants(Long educationId, String employeeId) {
//
//        Education education = educationRepository.findById(educationId)
//                .orElseThrow(() -> new IllegalArgumentException("교육을 찾을 수 없습니다."));
//
//        Employee employee = employeeRepository.findByEmployeeId(employeeId)
//                .orElseThrow(() -> new IllegalArgumentException("사원을 찾을 수 없습니다."));
//
//        // 현재 인원 수 증가
//        education.setCurrentParticipant(education.getCurrentParticipant() + 1);
//        educationRepository.save(education);
//
//        System.out.println("employee = " + employee);
//        System.out.println("education = " + education);
//
//        Course course = Course.builder()
//                .education(education)
//                .employee(employee)
//                .courseStatus(CourseStatus.FAIL)
//                .build();
//
//        System.out.println("course = " + course);
//
//        courseRepository.save(course);
//
//        return true;
//    }

    @Override
    public String incrementCurrentParticipants(Long educationId, String employeeId) {
        // 교육이 존재하는지 확인
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("교육을 찾을 수 없습니다."));

        // 사원이 존재하는지 확인
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("사원을 찾을 수 없습니다."));

        // 이미 신청한 교육이 있는지 확인
        if (courseRepository.existsByEducation_EducationIdAndEmployee_EmployeeId(educationId, employeeId)) {
            throw new IllegalStateException("이미 신청한 교육입니다."); // 중복 신청 시 예외 발생
        }

        // 현재 인원 수 증가
        education.setCurrentParticipant(education.getCurrentParticipant() + 1); // 참가자 수 증가
        educationRepository.save(education); // 변경된 교육 정보 저장

        // 교육 신청 처리
        Course course = Course.builder()
                .education(education)
                .employee(employee)
                .courseStatus(CourseStatus.FAIL) // 상태를 SUCCESS로 설정
                .build();

        courseRepository.save(course); // Course 엔티티 저장

        return "교육이 신청되었습니다."; // 성공 메시지 반환
    }

    // 교육 취소하기

    @Override
    @Transactional
    public Education createEducation(EducationRequestDTO requestDTO) {
        EducationCategory category = educationCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Education education = Education.builder()
                .educationName(requestDTO.getEducationName()) // 교육 이름
                .instructorName(requestDTO.getInstructorName()) // 강사 이름
                .institution(requestDTO.getInstitution()) // 교육 기관
                .startDate(LocalDate.from(requestDTO.getEducationStart())) // 교육 시작일
                .endDate(LocalDate.from(requestDTO.getEducationEnd())) // 교육 종료일
                .participants(requestDTO.getParticipants()) // 수강 정원
                .educationCategory(category) // 카테고리
                .educationCurriculum(requestDTO.getEducationCurriculum()) // 커리큘럼
                .build();

        return educationRepository.save(education);
    }

    @Override
    @Transactional
    public Education updateEducation(Long educationId, EducationRequestDTO requestDTO) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 ID : " + educationId));

        education.setEducationName(requestDTO.getEducationName()); // 교육 이름
        education.setInstitution(requestDTO.getInstitution()); // 교육 기관
        education.setInstructorName(requestDTO.getInstructorName()); // 강사 이름
        education.setStartDate(LocalDate.from(requestDTO.getEducationStart())); // 강의 시작일
        education.setEndDate(LocalDate.from(requestDTO.getEducationEnd())); // 강의 종료일

        return educationRepository.save(education);
    }

    @Override
    @Transactional
    public boolean cancelEducation(Long courseId) {
        // 교육 과정을 찾기
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Education education = course.getEducation();

            // 참가자 수 감소
            int currentParticipant = education.getCurrentParticipant(); // 현재 참가자 수 가져오기
            if (currentParticipant > 0) {
                education.setCurrentParticipant(currentParticipant - 1); // 참가자 수 -1
                educationRepository.save(education); // 변경 사항 저장
                System.out.println("현재 참가자 수 = " + (currentParticipant - 1)); // 감소한 참가자 수 출력
            } else {
                log.debug("참가자가 없습니다."); // 참가자가 없는 경우 출력
            }

            // 신청 정보 삭제 (해당 신청을 찾아서 삭제)
            courseRepository.delete(course); // course를 삭제합니다.

            return true; // 교육 취소 성공
        } else {
            return false; // 교육이 존재하지 않음
        }
    }

    @Override
    @Transactional
    public boolean deleteEducation(Long educationId) {
        if (educationRepository.existsById(educationId)) {
            educationRepository.deleteById(educationId);
            return true;
        }
        return false;
    }

}