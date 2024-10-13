package com.hq.heroes.auth.jwt;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String access = request.getHeader("access");

        if (access == null) {
            log.debug("요청 헤더에 액세스 토큰이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(access); // 토큰 만료 확인
        } catch (ExpiredJwtException e) {
            log.debug("액세스 토큰이 만료되었습니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(access);
        if (!category.equals("access")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String employeeId = jwtUtil.getUsername(access);
        String role = jwtUtil.getRole(access);

        // 사용자 정보 출력 (디버깅용)
        if (log.isDebugEnabled()) {
            log.debug("추출된 employeeId: {}, role: {}", employeeId, role);
        }

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setRole(Role.valueOf(role));

        CustomEmployeeDetails customEmployeeDetails = new CustomEmployeeDetails(employee);

        // UsernamePasswordAuthenticationToken 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customEmployeeDetails, null, customEmployeeDetails.getAuthorities());

        // Authentication 객체 설정 전 디버깅용 출력
        if (log.isDebugEnabled()) {
            log.debug("AuthToken: {}", authToken);
        }

        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 설정 후 확인
        if (log.isDebugEnabled()) {
            log.debug("SecurityContextHolder에 설정된 인증 정보: {}", SecurityContextHolder.getContext().getAuthentication());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                if (log.isDebugEnabled()) {
                    log.debug("사용자가 인증되었습니다: {}, 역할: {}", userDetails.getUsername(), userDetails.getRole());
                }
            } else {
                log.debug("Principal이 CustomEmployeeDetails 인스턴스가 아닙니다.");
            }
        } else {
            log.debug("인증된 사용자를 찾을 수 없습니다.");
        }

        filterChain.doFilter(request, response);
    }
}

