package com.hq.heroes.education.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.repository.CourseRepository;
import com.hq.heroes.education.repository.EducationCategoryRepository;
import com.hq.heroes.education.repository.EducationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용
class EducationServiceTest {

    @InjectMocks
    private EducationServiceImpl educationService; // 실제 테스트 대상인 서비스

    @Mock
    private EducationRepository educationRepository; // 모의 객체

    @Mock
    private EmployeeRepository employeeRepository; // 테스트에 사용할 mock 데이터

    @Mock
    private EducationCategoryRepository educationCategoryRepository;

    @Mock
    private CourseRepository courseRepository;

    Education education;
    Employee employee;
    EducationCategory category;


    @BeforeEach
    void setUp() {
        // Education
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

        category = EducationCategory.builder()
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

        // repository에 교육 정보 저장
//        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));
//        when(employeeRepository.findByEmployeeId("2024100001")).thenReturn(Optional.of(employee));


    }

    @Test
    @DisplayName("교육 상세 조회 테스트 - 성공")
    void getEducationById() {
        //when
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));
        Education result = educationService.getEducationById(1L);

        //then
        assertNotNull(result);
        assertEquals(education, result);
        System.out.println("강의명 = " + result.getEducationName());
        System.out.println("교육 카테고리 = " + result.getEducationCategory().getCategoryName());
        System.out.println("수강 인원 = " + result.getCurrentParticipant());
    }

    @Test
    @DisplayName("교육 정보 등록 테스트 - 성공")
    void createEducation() {
        // given
        // 기존 EducationCategory 객체를 생성하여 카테고리 이름을 설정
        EducationCategory category = new EducationCategory();
        category.setCategoryId(1L); // 카테고리 ID 설정
        category.setCategoryName("수학"); // 카테고리 이름 설정

        // 새로운 교육 정보 생성
        Education newEducation = Education.builder()
                .educationId(1L) // 새로운 교육 ID
                .educationName("수학 강의입니다~")
                .startDate(LocalDate.of(2024, 10, 28))
                .endDate(LocalDate.of(2024, 11, 28))
                .educationCategory(category)
                .participants(20)
                .currentParticipant(5)
                .institution("엔코아")
                .instructorName("이몽룡")
                .educationCurriculum("수학을 중점으로 배우는 강의입니다!")
                .build();

        // educationRepository가 save() 메서드 호출 시 newEducation을 반환하도록 설정
        when(educationRepository.save(newEducation)).thenReturn(newEducation);
        when(educationRepository.findById(1L)).thenReturn(Optional.of(newEducation));

        // when
        educationRepository.save(newEducation);
        Optional<Education> result = educationRepository.findById(1L);
        
        // then
        assertTrue(result.isPresent());
        assertEquals(newEducation, result.get());

        System.out.println("교육 명 = " + result.get().getEducationName());
        System.out.println("교육 카테고리 = " + result.get().getEducationCategory().getCategoryName());
        System.out.println("교육 커리큘럼 = " + result.get().getEducationCurriculum());
    }

    @Test
    @DisplayName("교육 정보 수정하기 테스트 - 성공")
    void updateEducation() {
        // given
        // 기존 교육 정보 가져오기
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));
        Education education = educationService.getEducationById(1L);

        // 새 카테고리 생성
        EducationCategory newCategory = new EducationCategory();
        newCategory.setCategoryId(2L); // 새로운 카테고리 ID
        newCategory.setCategoryName("국어"); // 새로운 카테고리 이름

        // 교육 정보 수정
        education.setEducationName("국어 수능 특강 강의");
        education.setEducationCategory(newCategory); // 카테고리 수정
        education.setEducationCurriculum("고3들을 위한 수업입니다.");

        // when
        educationRepository.save(education);

        Education updateEducation = educationService.getEducationById(1L);

        // then
        assertThat(updateEducation).isNotNull();
        assertThat(updateEducation.getEducationCategory()).isEqualTo(newCategory); // 새로운 카테고리와 비교
        System.out.println("수정된 교육 카테고리 = " + updateEducation.getEducationCategory().getCategoryName());

        assertThat(updateEducation.getEducationName()).isEqualTo(education.getEducationName());
        System.out.println("수정된 교육 명 = " + updateEducation.getEducationName());

        assertThat(updateEducation.getEducationCurriculum()).isEqualTo(education.getEducationCurriculum());
        System.out.println("수정된 교육 카테고리 = " + updateEducation.getEducationCurriculum());
    }

    @Test
    @DisplayName("교육 삭제 테스트 - 성공")
    void deleteEducation_Success() {
        // Given: 삭제할 교육이 존재하는 경우 설정
        when(educationRepository.existsById(1L)).thenReturn(true);

        // When: 교육 삭제 메서드 호출
        boolean isDeleted = educationService.deleteEducation(1L);

        // Then: 교육 삭제가 성공적으로 이루어졌는지 확인
        assertTrue(isDeleted);
        verify(educationRepository, times(1)).deleteById(1L);

        // 콘솔 로그 출력
        System.out.println("교육 ID " + 1L + "가 성공적으로 삭제되었습니다.");
    }

    @Test
    @DisplayName("교육 신청 테스트 - 성공")
    void applyForEducation_Success() {
        // Given
        String employeeId = "2024100001";
        Long educationId = 1L;

        // 특정 테스트 내에서만 모킹 설정
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));
        when(educationRepository.findById(educationId)).thenReturn(Optional.of(education));

        // When
        String result = educationService.incrementCurrentParticipants(educationId, employeeId);

        // Then
        assertEquals("교육이 신청되었습니다.", result);
        System.out.println("신청된 강의 명 = " + education.getEducationName());
        assertEquals(11, education.getCurrentParticipant()); // 참가자 수가 증가했는지 확인
        System.out.println("신청된 수강 인원 = " + education.getCurrentParticipant());
        verify(educationRepository, times(1)).save(education); // 저장이 호출되었는지 확인
    }

    @Test
    @DisplayName("교육 신청 테스트 - 실패(중복)")
    void applyForEducation_Fail_Duplicate() {
        // give
        // 이미 신청한 교육이 있는 경우를 설정
        when(courseRepository.existsByEducation_EducationIdAndEmployee_EmployeeId(1L, "2024100001"))
                .thenReturn(true);

        // 사원 정보도 추가로 설정
        when(employeeRepository.findByEmployeeId("2024100001")).thenReturn(Optional.of(employee));
        when(educationRepository.findById(1L)).thenReturn(Optional.of(education));

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            educationService.incrementCurrentParticipants(1L, "2024100001");
        });

        // then
        // 예외 메시지 확인
        assertEquals("이미 신청한 교육입니다.", exception.getMessage());

        // 추가적으로 상태가 변경되지 않았는지 확인
        assertEquals(10, education.getCurrentParticipant());
        System.out.println("수강 인원 = " + education.getCurrentParticipant());
    }

    @Test
    @DisplayName("교육 신청 테스트 - 실패 (교육 없음)")
    void applyForEducation_Fail_NoEducation() {
        // Given: 교육이 없는 경우 설정
        when(educationRepository.findById(999L)).thenReturn(Optional.empty());

        // When: 교육이 없는 경우 예외 발생 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            educationService.incrementCurrentParticipants(999L, "2024100001");
        });

        // Then: 예외 메시지 확인
        assertEquals("교육을 찾을 수 없습니다.", exception.getMessage()); // 기대하는 메시지로 변경

        // 상태 확인 및 콘솔 출력
        System.out.println("교육 ID: 999에 해당하는 교육이 존재하지 않습니다."); // 콘솔 출력
    }


}
