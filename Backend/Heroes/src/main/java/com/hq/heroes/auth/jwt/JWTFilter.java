package com.hq.heroes.auth.jwt;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

        String access = null;
        access = request.getHeader("access");

        // access token null
        if (access == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // access token expired
        try{
            jwtUtil.isExpired(access);
        } catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(access);

        // not access token
        if(!category.equals("access")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // email, role 값을 획득
        String employeeId = jwtUtil.getUsername(access);
        String role = jwtUtil.getRole(access);

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setRole(Role.valueOf(role));
        CustomEmployeeDetails customEmployeeDetails = new CustomEmployeeDetails(employee);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customEmployeeDetails, null, customEmployeeDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
        filterChain.doFilter(request, response);
    }

}
