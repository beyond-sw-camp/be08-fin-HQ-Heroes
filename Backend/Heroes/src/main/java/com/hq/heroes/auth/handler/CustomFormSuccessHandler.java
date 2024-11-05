package com.hq.heroes.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.auth.service.RefreshTokenService;
import com.hq.heroes.auth.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomFormSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenService refreshTokenService;
    private final EmployeeRepository employeeRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // ADMIN 권한일 경우 인증 코드 검증
        if (role.equals("ROLE_ADMIN")) {
            String authCode = request.getParameter("authCode"); // 인증 코드 파라미터에서 가져오기
            if (authCode == null || authCode.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"관리자 로그인을 이용해주세요.\"}");
                return;
            }

            // Redis에서 인증 코드 확인
            Optional<Employee> optionalEmail = employeeRepository.findByEmployeeId(username);
            if (optionalEmail.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"사용자 이메일을 찾을 수 없습니다.\"}");
                return;
            }

            Employee employee = optionalEmail.get();
            String email = employee.getEmail();
            String storedCode = redisTemplate.opsForValue().get(email);
            if (storedCode == null || !storedCode.equals(authCode)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Invalid auth code.\"}");
                return;
            }

            // 인증 코드가 유효하면 Redis에서 제거
            redisTemplate.delete(email);
        }

        // 일반 사용자이거나 인증 코드 검증이 완료된 경우 JWT 발급
        // access 토큰 30분
        String access = jwtUtil.createJwt("access", username, role, 30 * 60 * 1000L);
        response.setHeader("access", access);

        // refresh 1일
        Integer expireS = 24 * 60 * 60;
        String refresh = jwtUtil.createJwt("refresh", username, role, expireS * 1000L);

        // SameSite=None 설정과 함께 refresh 쿠키 추가
        Cookie refreshCookie = CookieUtil.createCookie("refresh", refresh, expireS);
        CookieUtil.addSameSiteCookie(refreshCookie, response);

        // refresh 토큰 DB 저장
        refreshTokenService.saveRefresh(username, expireS, refresh);

        // 응답 데이터 전송
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("employeeId", username);

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), responseData);
    }
}
