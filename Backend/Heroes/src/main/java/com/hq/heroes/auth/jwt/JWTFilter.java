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
            System.out.println("No access token found in request headers.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(access); // 토큰 만료 확인
        } catch (ExpiredJwtException e) {
            System.out.println("Access token has expired.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(access);
        if (!category.equals("access")) {
            System.out.println("Invalid token category: " + category);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String employeeId = jwtUtil.getUsername(access);
        String role = jwtUtil.getRole(access);

        // 사용자 정보 출력 (디버깅용)
        System.out.println("Extracted employeeId: " + employeeId + ", role: " + role);

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setRole(Role.valueOf(role));

        CustomEmployeeDetails customEmployeeDetails = new CustomEmployeeDetails(employee);

        // UsernamePasswordAuthenticationToken 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customEmployeeDetails, null, customEmployeeDetails.getAuthorities());

        // Authentication 객체 설정 전 디버깅용 출력
        System.out.println("AuthToken: " + authToken);

        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 설정 후 확인
        System.out.println("Authentication set in SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                System.out.println("User is authenticated: " + userDetails.getUsername() + ", Role: " + userDetails.getRole());
            } else {
                System.out.println("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            System.out.println("No authenticated user found.");
        }


        filterChain.doFilter(request, response);
    }



}
