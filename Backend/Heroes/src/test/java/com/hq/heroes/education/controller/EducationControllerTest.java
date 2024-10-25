package com.hq.heroes.education.controller;

import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.repository.CourseRepository;
import com.hq.heroes.education.repository.EducationRepository;
import com.hq.heroes.education.service.CourseServiceImpl;
import com.hq.heroes.education.service.EducationService;
import com.hq.heroes.education.service.EducationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EducationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Slf4j
public class EducationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EducationServiceImpl educationService;

    @Autowired
    private EducationService educationServiceImpl;

    @MockBean
    private CourseServiceImpl courseService;

    @MockBean
    private EducationRepository educationRepository;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("교육상세조회 GET /api/v1/education-service/education/{education-id} - 성공")
    void getEducationById() throws Exception {
        // given
        Education education = new Education();
        education.setEducationId(1L);
        education.setEducationName("수학 수업입니다!");

        // Education 객체를 DTO로 변화
        EducationResponseDTO educationDTO = education.toResponseDTO();

        // when
        Mockito.when(educationService.getEducationById(1L)).thenReturn(education);

        // then
        mockMvc.perform(get("/api/v1/education-service/education/{education-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // JSON 필드 검사
                .andExpect(jsonPath("$.educationId").value(educationDTO.getEducationId()))  // 올바르게 체이닝
                .andExpect(jsonPath("$.educationName").value(educationDTO.getEducationName()));

        System.out.println("강의 명 = " + educationDTO.getEducationName());

    }

    @Test
    @DisplayName("교육정보 등록 - 성공 POST /api/v1/education-service/education")
    void createEducation_Success() throws Exception {
        // Given: 테스트에 필요한 데이터를 준비
        String requestJson = "{ \"educationName\":\"프로그래밍 수업입니다.\", " +
                "\"instructorName\":\"홍길동\", " +
                "\"institution\":\"한국대학교\", " +
                "\"educationStart\":\"2024-01-01\", " +
                "\"educationEnd\":\"2024-01-10\", " +
                "\"participants\":30, " +
                "\"educationCurriculum\":\"기초 프로그래밍 커리큘럼\", " +
                "\"categoryId\":1 }"; // 카테고리 ID 포함

        Education education = new Education();
        education.setEducationId(1L);
        education.setEducationName("프로그래밍 수업입니다.");
        education.setInstructorName("홍길동");
        education.setInstitution("한국대학교");
        education.setStartDate(LocalDate.of(2024, 1, 1));
        education.setEndDate(LocalDate.of(2024, 1, 10));
        education.setParticipants(30);
        education.setEducationCurriculum("기초 프로그래밍 커리큘럼");

        // 서비스 레이어의 동작을 모킹
        given(educationService.createEducation(any(EducationRequestDTO.class))).willReturn(education);

        // When: 실제 요청을 수행
        mockMvc.perform(post("/api/v1/education-service/education")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // Then: 결과를 검증
                .andExpect(status().isCreated())  // HTTP 201 상태 확인
                .andExpect(jsonPath("$.educationId").value(1L))  // 응답 내용 확인
                .andExpect(jsonPath("$.educationName").value("프로그래밍 수업입니다."))
                .andExpect(jsonPath("$.instructorName").value("홍길동"))
                .andExpect(jsonPath("$.institution").value("한국대학교"))
                .andExpect(jsonPath("$.educationStart").value("2024-01-01"))
                .andExpect(jsonPath("$.educationEnd").value("2024-01-10"))
                .andExpect(jsonPath("$.participants").value(30))
                .andExpect(jsonPath("$.educationCurriculum").value("기초 프로그래밍 커리큘럼"));
    }

    @Test
    @DisplayName("교육정보수정 - 성공 PUT /api/v1/education-service/education/{education-id} - 성공")
    void updateEducation_Success() throws Exception {
        // Given: 테스트에 필요한 데이터를 준비
        Long educationId = 1L; // 수정할 교육 ID
        String requestJson = "{\"educationName\":\"프로그래밍 수업입니다.\", \"instructorName\":\"홍길동\"}";

        Education education = new Education();
        education.setEducationId(educationId);
        education.setEducationName("프로그래밍 수업입니다.");
        education.setInstructorName("홍길동");

        // 서비스 레이어의 동작을 모킹
        given(educationService.updateEducation(eq(educationId), any(EducationRequestDTO.class))).willReturn(education);

        System.out.println("테스트를 시작합니다: 교육 정보 수정");

        // When: 실제 요청을 수행
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/education-service/education/{education-id}", educationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // Then: 결과를 검증
                .andExpect(status().isOk())  // HTTP 200 상태 확인
                .andExpect(jsonPath("$.educationId").value(educationId))  // 응답 내용 확인
                .andExpect(jsonPath("$.educationName").value("프로그래밍 수업입니다."))
                .andExpect(jsonPath("$.instructorName").value("홍길동"));

        System.out.println("교육 정보가 성공적으로 수정되었습니다.");
        System.out.println("수정된 교육 정보 조회: ID=" + education.getEducationId() + ", 이름=" + education.getEducationName());
    }

    @Test
    @DisplayName("교육정보수정 - 실패 PUT /education/{education-id} - 존재하지 않는 교육 ID")
    void updateEducation_NotFound() throws Exception {
        Long invalidId = 999L; // 존재하지 않는 ID

        // EducationRequestDTO 객체 직접 생성 및 초기화
        EducationRequestDTO educationRequestDTO = new EducationRequestDTO();
        educationRequestDTO.setEducationName("Updated Name");
        educationRequestDTO.setInstructorName("Updated Instructor");
        educationRequestDTO.setInstitution("Updated Institution");

        // 예외 발생을 위한 Mock 설정
        when(educationService.updateEducation(eq(invalidId), any(EducationRequestDTO.class)))
                .thenReturn(null); // 교육이 없으므로 null 반환

        mockMvc.perform(put("/education/{education-id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"educationName\": \"Updated Name\"}")) // 요청 본문 설정
                .andExpect(status().isNotFound()); // 404 상태 코드 예상
    }


    @Test
    @DisplayName("교육삭제 - 성공 DELETE /api/v1/education-service/education/{education-id} - 성공")
    public void deleteEducation_Success() throws Exception {
        // Given
        Long educationId = 1L;
        when(educationService.deleteEducation(anyLong())).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/education-service/education/{education-id}", educationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("교육삭제 - 실패 DELETE /api/v1/education-service/education/{education-id} - 교육이 존재하지 않는다")
    public void deleteEducation_Fail() throws Exception {
        // Given
        Long educationId = 1L;
        when(educationService.deleteEducation(anyLong())).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/education-service/education/{education-id}", educationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("교육신청 - 성공 POST /api/v1/education-service/apply/{educationId}/{employeeId} - 성공")
    public void applyForEducation_Success() throws Exception {
        // Given
        Long educationId = 1L;
        String employeeId = "2024100001";
        String successMessage = "교육이 신청되었습니다.";
        when(educationServiceImpl.incrementCurrentParticipants(anyLong(), anyString())).thenReturn(successMessage); // Mock 객체로 처리

        // When & Then
        mockMvc.perform(post("/api/v1/education-service/apply/{educationId}/{employeeId}", educationId, employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(successMessage));
    }

    @Test
    @DisplayName("교육신청 - 실패 POST /api/v1/education-service/apply/{educationId}/{employeeId} - 실패(중복 신청)")
    public void applyForEducation_Fail_Duplicate() throws Exception {
        // Given
        Long educationId = 1L;
        String employeeId = "2024100001";
        when(educationService.incrementCurrentParticipants(anyLong(), any())).thenThrow(new IllegalStateException("이미 신청한 교육입니다."));

        // When & Then
        mockMvc.perform(post("/api/v1/education-service/apply/{educationId}/{employeeId}", educationId, employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 신청한 교육입니다."));
    }

    @Test
    @DisplayName("교육신청 - 실패 POST /api/v1/education-service/apply/{educationId}/{employeeId} - 실패(교육이 존재하지 않음)")
    public void applyForEducation_Fail_EducationNotFound() throws Exception {
        // Given
        Long educationId = 1L;
        String employeeId = "2024100001";
        when(educationService.incrementCurrentParticipants(anyLong(), any())).thenThrow(new IllegalArgumentException("교육을 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(post("/api/v1/education-service/apply/{educationId}/{employeeId}", educationId, employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("교육을 찾을 수 없습니다."));
    }

//    @Test
//    @DisplayName("교육신청 - 실패 POST /api/v1/education-service/apply/{educationId}/{employeeId} - 실패(서버 오류)")
//    public void applyForEducation_Fail_Error() throws Exception {
//        // Given
//        Long educationId = 1L;
//        String employeeId = "2024100001";
//        when(educationService.incrementCurrentParticipants(anyLong(), any())).thenThrow(new RuntimeException("서버 오류"));
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/education-service/apply/{educationId}/{employeeId}", educationId, employeeId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."));
//    }

}