package com.hq.heroes.common.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.common.dto.EmailMessage;
import com.hq.heroes.common.dto.EmailReqDTO;
import com.hq.heroes.common.dto.EmailResDTO;
import com.hq.heroes.common.dto.ResetPWDTO;
import com.hq.heroes.common.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mails")
@Tag(name = "메일 서비스 APIs", description = "메일 서비스 Api 리스트")
public class EmailController {

    private final EmailService emailService;
    private final EmployeeRepository employeeRepository;

    // 임시 비밀번호 발급
    @PostMapping("/password")
    @Operation(summary = "임시 비밀번호 발급", description = "임시 비밀번호를 발급하여 메일로 전송한다.")
    public ResponseEntity<String> sendPasswordMail(@RequestBody ResetPWDTO resetPWDTO) {
        Optional<Employee> employee = employeeRepository.findByEmployeeId(resetPWDTO.getEmployeeId());

        // employee가 존재하지 않으면 "존재하지 않는 직원입니다." 메시지 반환
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 직원입니다.");
        }

        String email = employee.get().getEmail();
        String name = employee.get().getEmployeeName();

        // name과 resetPWDTO.getName()이 일치하지 않으면 "이름이 다릅니다." 메시지 반환
        if (!name.equals(resetPWDTO.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이름이 다릅니다.");
        }

        // 임시 비밀번호 발급 이메일 전송 로직
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("[HQ-HeRoes] 임시 비밀번호 발급")
                .build();

        emailService.sendMail(emailMessage, "/mails/resetPassword");

        return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
    }



    // 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
    @PostMapping("/email")
    @Operation(summary = "인증 코드 발급", description = "인증 코드를 발급하여 메일로 전송한다.")
    public ResponseEntity<String> sendAuthCodeMail(@RequestBody EmailReqDTO emailReqDTO) {

        // 사원 존재 여부 확인
        Optional<Employee> employee = employeeRepository.findByEmployeeId(emailReqDTO.getEmployeeId());

        // 사원이 존재하지 않을 경우 메시지 반환
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 사원입니다.");
        }

        String email = employee.get().getEmail();

        // 인증 코드 메일 발송
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("[HQ-HeRoes] 이메일 인증을 위한 인증 코드 발송")
                .build();

        emailService.sendMail(emailMessage, "/mails/authCode");

        // 성공 메시지 반환
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }


}
