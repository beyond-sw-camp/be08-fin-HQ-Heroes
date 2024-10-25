package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.certification.entity.Certification;
import com.hq.heroes.certification.repository.CertificationRepository;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용
class CertificationServiceTest {
    @InjectMocks
    private CertificationServiceImpl certificationService;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    Certification certification;
    Department department;

    @BeforeEach
    void setUp() {
        Team team1 = Team.builder()
                .teamId(1L)
                .teamName("영업1팀")
                .build();

        Team team2 = Team.builder()
                .teamId(2L)
                .teamName("영업2팀")
                .build();

        List<Team> teams1 = new ArrayList<>();
        teams1.add(team1);
        teams1.add(team2);

        department = Department.builder()
                .deptId(1L)
                .deptName("영업부")
                .teams(teams1)
                .build();

        certification = Certification.builder()
                .certificationId(1L)
                .certificationName("정보처리기사")
                .institution("한국산업인력공단")
                .benefit("10만원 상품권")
                .applicationStartDate(LocalDate.of(2024, 10, 30))
                .applicationEndDate(LocalDate.of(2024, 11, 10))
                .examDate(LocalDate.of(2024, 11, 28))
                .department(department)
                .build();
    }

    @Test
    @DisplayName("자격증 상세 조회 테스트 - 성공")
    void getCertificationById() {
        // when
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
        Certification result = certificationService.getCertificationById(1L);

        // then
        assertNotNull(result);
        assertEquals(certification, result);
        System.out.println("자격증 명 = " + result.getCertificationName());
        System.out.println("발급기관 = " + result.getInstitution());
        System.out.println("시험 날짜 = " + result.getExamDate());
    }

    @Test
    @DisplayName("자격증 정보 등록 - 성공")
    void createCertification() {
        // 부서 객체 초기화
        Department department = new Department();
        department.setDeptId(1L); // 유효한 ID 설정
        department.setDeptName("IT부서");

        // Mocking: CertificationRepository의 save 메서드 설정
        Certification newcertification = Certification.builder()
                .certificationId(1L)
                .certificationName("포토샵 1급")
                .institution("한국생산성본부")
                .department(department)
                .build();

        when(certificationRepository.save(any(Certification.class))).thenReturn(newcertification);
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(newcertification));

        // when
        certificationRepository.save(newcertification);
        Optional<Certification> result = certificationRepository.findById(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals(newcertification.getCertificationName(), result.get().getCertificationName());
        assertEquals(newcertification.getInstitution(), result.get().getInstitution());
        assertEquals(newcertification.getDepartment(), result.get().getDepartment());

        System.out.println("교육명 = " + result.get().getCertificationName());
        System.out.println("발급기관 = " + result.get().getInstitution());
        System.out.println("부서명 = " + result.get().getDepartment().getDeptName());
    }

@Test
@DisplayName("자격증 정보 수정하기 테스트 - 성공")
void updateCertification() {
    // 업데이트할 요청 DTO
    CertificationRequestDTO requestDTO = new CertificationRequestDTO();
    requestDTO.setCertificationName("포토샵 2급");
    requestDTO.setInstitution("한국생산성본부");
    requestDTO.setBenefit("시험 응시 무료");
    requestDTO.setApplicationStartDate(LocalDate.of(2024, 1, 1));
    requestDTO.setApplicationEndDate(LocalDate.of(2024, 12, 31));
    requestDTO.setExamDate(LocalDate.of(2024, 5, 15));
    requestDTO.setDeptId(1L); // 부서 ID 설정

    // Mocking: certificationRepository의 findById 메서드 설정
    when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));
    // Mocking: certificationRepository의 save 메서드 설정
    when(certificationRepository.save(certification)).thenReturn(certification);

    // when
    Certification updatedCertification = certificationService.updateCertification(1L, requestDTO);

    // then
    assertNotNull(updatedCertification);
    assertEquals("포토샵 2급", updatedCertification.getCertificationName());
    assertEquals("한국생산성본부", updatedCertification.getInstitution());
    assertEquals(LocalDate.of(2024, 1, 1), updatedCertification.getApplicationStartDate());
    assertEquals(LocalDate.of(2024, 12, 31), updatedCertification.getApplicationEndDate());
    assertEquals(LocalDate.of(2024, 5, 15), updatedCertification.getExamDate());
    assertEquals(department, updatedCertification.getDepartment());

    // verify
    verify(certificationRepository).findById(1L);
    verify(certificationRepository).save(certification);
}

    @Test
    @DisplayName("자격증 정보 수정하기 테스트 - 실패")
    void updateCertification_NotFound() {
        // 업데이트할 요청 DTO
        CertificationRequestDTO requestDTO = new CertificationRequestDTO();
        requestDTO.setCertificationName("포토샵 2급");

        // Mocking: certificationRepository의 findById 메서드 설정
        when(certificationRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            certificationService.updateCertification(1L, requestDTO);
        });

        assertEquals("존재하지 않는 자격증 ID : 1", exception.getMessage());

        // verify
        verify(certificationRepository).findById(1L);
        verify(certificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("자격증 삭제 테스트 - 성공")
    void deleteCertification_Success() {
        // Mocking: certificationRepository의 existsById 메서드 설정
        when(certificationRepository.existsById(1L)).thenReturn(true);

        // Mocking: certificationRepository의 deleteById 메서드 설정
        doNothing().when(certificationRepository).deleteById(1L);

        // when
        boolean result = certificationService.deleteCertification(1L);

        // then
        assertTrue(result);

        // verify
        verify(certificationRepository).existsById(1L);
        verify(certificationRepository).deleteById(1L);
    }

    @Test
    @DisplayName("자격증 삭제 테스트 - 실패")
    void deleteCertification_NotFound() {
        // Mocking: certificationRepository의 existsById 메서드 설정
        when(certificationRepository.existsById(1L)).thenReturn(false);

        // when
        boolean result = certificationService.deleteCertification(1L);

        // then
        assertFalse(result);

        // verify
        verify(certificationRepository).existsById(1L);
        verify(certificationRepository, never()).deleteById(any());
    }


}