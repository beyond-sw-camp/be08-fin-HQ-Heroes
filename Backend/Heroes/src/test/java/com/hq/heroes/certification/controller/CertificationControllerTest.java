package com.hq.heroes.certification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.certification.entity.Certification;
import com.hq.heroes.certification.repository.CertificationRepository;
import com.hq.heroes.certification.service.CertificationService;
import com.hq.heroes.certification.service.CertificationServiceImpl;
import com.hq.heroes.employee.entity.Department;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = CertificationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class CertificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CertificationService certificationService1;

    @MockBean
    private CertificationServiceImpl certificationService;

    @MockBean
    private CertificationRepository certificationRepository;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("자격증 상세 조회 GET /api/v1/certification-service/certification/{certification-id} - 성공")
    void getCertificationById_Success() throws Exception {
        Long certificationId = 1L; // 존재하는 자격증 ID
        Certification certification = new Certification();
        certification.setCertificationId(1L);
        certification.setCertificationName("Test Certification");
        certification.setInstitution("Test Institution");

        // 자격증 조회 성공 시의 반환값 설정
        when(certificationService.getCertificationById(certificationId)).thenReturn(certification);

        mockMvc.perform(get("/api/v1/certification-service/certification/{certification-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK 응답 예상
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)); // JSON 응답 타입 확인

        verify(certificationService, times(1)).getCertificationById(1L); // 서비스 메서드 호출 확인
    }

    @Test
    @DisplayName("자격증 등록 POST /api/v1/certification-service/certification")
    void createCertification_Success() throws Exception {
        // given
        CertificationRequestDTO requestDTO = new CertificationRequestDTO();
        requestDTO.setCertificationName("정보처리기사");
        requestDTO.setInstitution("한국공사");
        requestDTO.setBenefit("취업 시 유리");
        requestDTO.setApplicationStartDate(LocalDate.of(2024, 1, 1));
        requestDTO.setApplicationEndDate(LocalDate.of(2024, 2, 1));
        requestDTO.setExamDate(LocalDate.of(2024, 3, 1));
        requestDTO.setDeptId(1L); // 예시 부서 ID

        Department department = new Department();
        department.setDeptName("정보기술부서"); // 예시 부서 이름
        department.setDeptId(1L); // 예시 부서 ID

        Certification certification = new Certification();
        certification.setCertificationId(1L);
        certification.setCertificationName(requestDTO.getCertificationName());
        certification.setInstitution(requestDTO.getInstitution());
        certification.setBenefit(requestDTO.getBenefit());
        certification.setApplicationStartDate(requestDTO.getApplicationStartDate());
        certification.setApplicationEndDate(requestDTO.getApplicationEndDate());
        certification.setExamDate(requestDTO.getExamDate());
        certification.setDepartment(department); // 부서 설정

        CertificationResponseDTO responseDTO = certification.toResponseDTO();

        // when
        Mockito.when(certificationService.createCertification(any(CertificationRequestDTO.class)))
                .thenReturn(certification);

        // then
        mockMvc.perform(post("/api/v1/certification-service/certification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.certificationId").value(responseDTO.getCertificationId()))
                .andExpect(jsonPath("$.certificationName").value(responseDTO.getCertificationName()))
                .andExpect(jsonPath("$.institution").value(responseDTO.getInstitution()))
                .andExpect(jsonPath("$.benefit").value(responseDTO.getBenefit()))
                .andExpect(jsonPath("$.applicationStartDate").value(responseDTO.getApplicationStartDate().toString()))
                .andExpect(jsonPath("$.applicationEndDate").value(responseDTO.getApplicationEndDate().toString()))
                .andExpect(jsonPath("$.examDate").value(responseDTO.getExamDate().toString()))
                .andExpect(jsonPath("$.deptName").value(responseDTO.getDeptName()));
    }

    @Test
    @DisplayName("자격증 정보 수정 PUT /api/v1/certification-service/certification/{certification-id}")
    void updateCertification_Success() throws Exception {
        // given
        Long certificationId = 1L;

        CertificationRequestDTO requestDTO = new CertificationRequestDTO();
        requestDTO.setCertificationName("정보처리기사");
        requestDTO.setInstitution("한국공사");
        requestDTO.setBenefit("취업 시 유리");
        requestDTO.setApplicationStartDate(LocalDate.of(2024, 1, 1));
        requestDTO.setApplicationEndDate(LocalDate.of(2024, 2, 1));
        requestDTO.setExamDate(LocalDate.of(2024, 3, 1));
        requestDTO.setDeptId(1L); // 부서 ID 설정

        Department department = new Department();
        department.setDeptName("정보기술부서");
        department.setDeptId(1L);

        Certification updatedCertification = Certification.builder()
                .certificationId(certificationId)
                .certificationName(requestDTO.getCertificationName())
                .institution(requestDTO.getInstitution())
                .benefit(requestDTO.getBenefit())
                .applicationStartDate(requestDTO.getApplicationStartDate())
                .applicationEndDate(requestDTO.getApplicationEndDate())
                .examDate(requestDTO.getExamDate())
                .department(department)
                .build();

        CertificationResponseDTO responseDTO = updatedCertification.toResponseDTO();

        // when
        Mockito.when(certificationService.updateCertification(eq(certificationId), any(CertificationRequestDTO.class)))
                .thenReturn(updatedCertification);

        // then
        mockMvc.perform(put("/api/v1/certification-service/certification/{certification-id}", certificationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationId").value(responseDTO.getCertificationId()))
                .andExpect(jsonPath("$.certificationName").value(responseDTO.getCertificationName()))
                .andExpect(jsonPath("$.institution").value(responseDTO.getInstitution()))
                .andExpect(jsonPath("$.benefit").value(responseDTO.getBenefit()))
                .andExpect(jsonPath("$.applicationStartDate").value(responseDTO.getApplicationStartDate().toString()))
                .andExpect(jsonPath("$.applicationEndDate").value(responseDTO.getApplicationEndDate().toString()))
                .andExpect(jsonPath("$.examDate").value(responseDTO.getExamDate().toString()))
                .andExpect(jsonPath("$.deptName").value(responseDTO.getDeptName()));
    }

    @Test
    public void deleteCertification_Success() throws Exception {
        Long certificationId = 1L; // 존재하는 ID

        // 삭제가 성공적으로 이루어질 것이라고 가정
        when(certificationService.deleteCertification(certificationId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/certification-service/certification/{certification-id}", certificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 200 OK 응답 예상

        verify(certificationService, times(1)).deleteCertification(certificationId); // 서비스 메서드 호출 확인
    }

    @Test
    public void deleteCertification_NotFound() throws Exception {
        Long certificationId = 999L; // 존재하지 않는 ID

        // 삭제가 실패할 것이라고 가정
        when(certificationService.deleteCertification(certificationId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/certification-service/certification/{certification-id}", certificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404 NOT FOUND 응답 예상

        verify(certificationService, times(1)).deleteCertification(certificationId); // 서비스 메서드 호출 확인
    }


}
