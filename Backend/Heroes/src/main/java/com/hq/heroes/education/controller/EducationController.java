package com.hq.heroes.education.controller;

import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.service.CourseService;
import com.hq.heroes.education.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/education-service")
@Tag(name = "Education APIs", description = "교육 관련 API 목록")
public class EducationController {
    private final EducationService educationService;
    private final CourseService courseService;

    // 교육 목록 조회하기
    @GetMapping("/education")
    @Operation(summary = "교육 목록 조회", description = "전체 교육의 목록을 조회한다.")
    public ResponseEntity<List<EducationResponseDTO>> getEducations() {
        List<Education> educations = educationService.getEducations(); // 수정된 부분
        List<EducationResponseDTO> educationDTOs = educations.stream().map(Education::toResponseDTO).collect(Collectors.toList());

        if (!educations.isEmpty()) {
            return new ResponseEntity<>(educationDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(educationDTOs, HttpStatus.NOT_FOUND);
        }
    }

    // 교육 목록 상세 조회하기 - 테스트
    @GetMapping("education/{education-id}")
    @Operation(summary = "교육 상세 조회", description = "교육 ID로 해당 교육의 정보를 조회한다.")
    public ResponseEntity<EducationResponseDTO> getEducationById(
            @Parameter(description = "교육 ID", example = "1") @PathVariable("education-id") Long educationId) {
        Education education = educationService.getEducationById(educationId);

        if (education != null) {
            EducationResponseDTO educationDTO = education.toResponseDTO();
            return new ResponseEntity<>(educationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 교육 신청하기 - 테스트
    @PostMapping("/apply/{educationId}/{employeeId}")
    public ResponseEntity<?> applyForEducation(@PathVariable Long educationId, @PathVariable String employeeId) {
        try {
            // 신청 인원 수 증가 로직 (중복 신청 시 IllegalStateException 발생)
            educationService.incrementCurrentParticipants(educationId, employeeId);

            // 성공 시 JSON 응답
            return ResponseEntity.ok(Collections.singletonMap("message", "교육이 신청되었습니다."));

        } catch (IllegalStateException e) {
            // 이미 신청한 교육인 경우 409 상태와 함께 메시지 반환
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", "이미 신청한 교육입니다."));

        } catch (IllegalArgumentException e) {
            // 교육 또는 사원을 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));

        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "서버 오류가 발생했습니다."));
        }
    }

    // 교육 정보 등록 - 테스트
    @PostMapping("/education")
    @Operation(summary = "교육 등록", description = "교육 정보를 받아서 등록한다.")
    public ResponseEntity<EducationResponseDTO> create(@RequestBody EducationRequestDTO requestDTO) {

        Education education = educationService.createEducation(requestDTO);
        return new ResponseEntity<>(education.toResponseDTO(), HttpStatus.CREATED);
    }

    // 교육 정보 수정 - 테스트
    @PutMapping("/education/{education-id}")
    @Operation(summary = "교육 목록 수정", description = "교육 정보를 받아 수정한다.")
    public ResponseEntity<EducationResponseDTO> update (
                @Parameter(description = "교육 ID", example = "1")
                @PathVariable("education-id") Long educationId,
                @RequestBody EducationRequestDTO requestDTO) {
            Education education = educationService.updateEducation(educationId, requestDTO);

            if (education != null) {
                return new ResponseEntity<>(education.toResponseDTO(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }

    // 교육 삭제 -테스트
    @DeleteMapping("/education/{education-id}")
    @Operation(summary = "교육 삭제", description = "교육 ID로 해당 교욱을 삭제한다.")
    public ResponseEntity<Void> delete (
            @Parameter(description = "교육 ID", example = "1") @PathVariable("education-id") Long educationId) {

        boolean isDeleted = educationService.deleteEducation(educationId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
