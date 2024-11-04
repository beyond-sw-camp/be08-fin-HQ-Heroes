package com.hq.heroes.common.service;

import com.hq.heroes.common.dto.EmailMessage;
import com.hq.heroes.employee.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final RedisTemplate<String, String> redisTemplate;  // RedisTemplate 주입
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final EmployeeService employeeService;

    // Redis 연결 테스트 메서드
    @PostConstruct
    public void testRedisConnection() {
        String testKey = "testKey";
        String testValue = "testValue";

        redisTemplate.opsForValue().set(testKey, testValue, 1, TimeUnit.MINUTES);
        String result = redisTemplate.opsForValue().get(testKey);

        if (testValue.equals(result)) {
            log.info("✅ Redis 연결 및 데이터 저장/조회 성공: {}", result);
        } else {
            log.error("❌ Redis 데이터 저장/조회 실패");
        }
    }

    @Override
    @Transactional
    public void sendMail(EmailMessage emailMessage, String type) {
        String authCode = createCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // 비밀번호 재설정 요청 처리
        if (type.equals("mails/resetPassword")) {
            employeeService.setTempPassword(emailMessage.getTo(), authCode);
        }
        // 이메일 인증 코드 요청 처리
        else if (type.equals("mails/authCode")) {
            // Redis에 인증 코드 저장
            String email = emailMessage.getTo();
            redisTemplate.opsForValue().set(email, authCode, 3, TimeUnit.MINUTES);  // 3분 만료 시간 설정

            String checkAuthCode = getAuthCode(email); // 인증 코드 조회
            log.info("✅ Redis 저장된 인증 코드: {}", checkAuthCode);
        }

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 메일 수신자 설정
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목 설정
            mimeMessageHelper.setText(setContext(authCode, type), true); // 메일 본문 설정 (HTML 여부)
            javaMailSender.send(mimeMessage);
            log.info("이메일 전송 성공");
        } catch (MessagingException e) {
            log.error("이메일 전송 실패", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        // 6자리 숫자를 생성
        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10)); // 0부터 9까지의 숫자 중 무작위 선택
        }

        return key.toString(); // 최종적으로 6자리 숫자 반환
    }

    // thymeleaf를 통해 HTML 이메일 본문 생성
    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);  // 인증 코드를 context에 설정
        return templateEngine.process(type, context);  // 템플릿 엔진으로 처리
    }

    public String getAuthCode(String email) {
        return redisTemplate.opsForValue().get(email); // Redis에서 이메일을 키로 인증 코드 조회
    }

}