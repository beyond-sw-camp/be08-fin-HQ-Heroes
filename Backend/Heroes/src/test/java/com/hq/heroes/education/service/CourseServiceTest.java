package com.hq.heroes.education.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.entity.enums.CourseStatus;
import com.hq.heroes.education.repository.CourseRepository;
import com.hq.heroes.education.repository.EducationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용
class CourseServiceTest {

    @InjectMocks
    private CourseServiceImpl courseService; // 실제 테스트 대상인 서비스

    @Mock
    private CourseRepository courseRepository; // 모의 객체

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private Employee employee;
    private Education education;
    private Course course;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("hong@naver.com")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .build();

        EducationCategory category = EducationCategory.builder()
                .categoryId(1L)
                .categoryName("어학")
                .build();

        education = Education.builder()
                .educationId(1L)
                .instructorName("홍길동")
                .educationName("영어 강의")
                .institution("한화시스템")
                .startDate(LocalDate.of(2024, 10, 30))
                .endDate(LocalDate.of(2024, 11, 30))
                .participants(30)
                .currentParticipant(10)
                .educationCurriculum("영어 강의 진행합니다~")
                .educationCategory(category)
                .build();

        course = Course.builder()
                .courseId(1L)
                .courseStatus(CourseStatus.FAIL)
                .employee(employee)
                .education(education)
                .build();
    }

    @Test
    @DisplayName("사원 ID로 신청한 교육 목록 조회 테스트 - 성공")
    void getCourseByEmployeeId() {
        // given
        String employeeId = "2024100001"; // 사원 번호 받아오기
        when(courseRepository.findByEmployee_EmployeeId(employeeId)).thenReturn(Collections.singletonList(course)); // 특정 사원 ID에 대한 수업 내역이 course 하나라고 가정

        // when
        List<Course> result = courseService.getCourseByEmployeeId(employeeId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(course.getCourseId(), result.get(0).getCourseId());
        System.out.println("수강 ID: " + result.get(0).getCourseId());
        assertEquals(course.getCourseStatus(), result.get(0).getCourseStatus());
        System.out.println("강의 상태 = " + result.get(0).getCourseStatus());
        assertEquals(course.getEducation().getEducationName(), result.get(0).getEducation().getEducationName());
        System.out.println("강의 명 = " + result.get(0).getEducation().getEducationName());
    }

    @Test
    @DisplayName("교육 이수 - 성공")
    public void completeCourse_Success() {
        // Given
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // When
        courseService.completeCourse(courseId);

        // Then
        assertEquals(CourseStatus.PASS, course.getCourseStatus()); // 상태가 이수로 변경되었는지 확인
        System.out.println("교육 상태 = " + course.getCourseStatus());

        verify(courseRepository, times(1)).save(course); // save 메서드가 한 번 호출되었는지 확인
    }


}
