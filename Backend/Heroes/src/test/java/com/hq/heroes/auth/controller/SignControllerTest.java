package com.hq.heroes.auth.controller;

import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.service.JoinService;
import com.hq.heroes.common.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignController.class)
@Import(TestSecurityConfig.class)  // 시큐리티 설정을 가져옴
class SignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JoinService joinService;

    @MockBean
    private JWTUtil jwtUtil;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    @WithMockUser  // MockUser로 인증된 상태에서 테스트 실행
    void joinSuccess() throws Exception {
        // given: JoinDTO 생성 및 프로필 이미지 파일 모킹
        JoinDTO joinDTO = JoinDTO.builder()
                .employeeName("홍길동")
                .email("hong@naver.com")
                .password("password")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .phoneNumber("01012345678")
                .roadAddress("서울 동작구 보라매로 87")
                .lotAddress("서울 동작구 신대방동 344-4")
                .detailedAddress("5층")
                .deptId(1L)
                .teamId(1L)
                .positionId(1L)
                .jobRoleId(1L)
                .profileImage(new MockMultipartFile("profileImage", "test.jpg", "image/jpeg", new byte[0]))  // 프로필 이미지 모킹
                .build();

        // 회원가입 서비스 결과 모킹
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("username", joinDTO.getEmployeeName());
        response.put("joinResult", "가입 성공");

        // 서비스에서 성공 응답 반환하도록 모킹
        Mockito.when(joinService.join(any(JoinDTO.class)))
                .thenReturn("가입 성공");

        // when & then: 회원가입 요청 및 성공 응답 검증
        mockMvc.perform(multipart("/join")
                        .file("profileImage", new byte[0])  // 이미지 파일 추가
                        .param("employeeName", joinDTO.getEmployeeName())
                        .param("email", joinDTO.getEmail())
                        .param("password", joinDTO.getPassword())
                        .param("role", joinDTO.getRole().name())
                        .param("joinDate", joinDTO.getJoinDate().toString())
                        .param("annualLeave", joinDTO.getAnnualLeave().toString())
                        .param("status", joinDTO.getStatus().name())
                        .param("birthDate", joinDTO.getBirthDate().toString())
                        .param("phoneNumber", joinDTO.getPhoneNumber())
                        .param("roadAddress", joinDTO.getRoadAddress())
                        .param("lotAddress", joinDTO.getLotAddress())
                        .param("detailedAddress", joinDTO.getDetailedAddress())
                        .param("deptId", joinDTO.getDeptId().toString())
                        .param("teamId", joinDTO.getTeamId().toString())
                        .param("positionId", joinDTO.getPositionId().toString())
                        .param("jobRoleId", joinDTO.getjobRoleId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())  // 성공 상태 코드 200
                .andExpect(jsonPath("$.success").value(true))  // 응답 JSON 필드 검증
                .andExpect(jsonPath("$.username").value("홍길동"))
                .andExpect(jsonPath("$.joinResult").value("가입 성공"));
    }

    @Test
    @DisplayName("잘못된 이미지 파일 업로드 테스트")
    @WithMockUser
    void invalidImageUpload() throws Exception {
        // given: 잘못된 이미지 파일 (MIME 타입이 이미지가 아님)
        MockMultipartFile invalidImageFile = new MockMultipartFile(
                "profileImage", "test.txt", "text/plain", "invalid data".getBytes());

        // when & then: 잘못된 이미지 파일 업로드 시도 및 실패 응답 검증
        mockMvc.perform(multipart("/join")
                        .file(invalidImageFile)
                        .param("employeeName", "홍길동")
                        .param("email", "hong@naver.com")
                        .param("password", "password")
                        .param("role", Role.ROLE_USER.name())
                        .param("joinDate", "2024-10-23")
                        .param("birthDate", "1990-05-20")
                        .param("phoneNumber", "01012345678")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())  // 실패 상태 코드 400
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미지 파일만 업로드할 수 있습니다."));
    }
}
