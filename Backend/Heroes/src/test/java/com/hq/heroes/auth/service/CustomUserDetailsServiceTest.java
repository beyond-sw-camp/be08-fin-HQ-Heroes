package com.hq.heroes.auth.service;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)  // Mockito 사용
class CustomUserDetailsServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("활설화된 사용자의 정보를 불러오는 테스트")
    void loadUserByUsername_success() throws Exception {
        //given
        String id = "2024100001";
        Employee activeEmployee = new Employee();
        activeEmployee.setEmployeeId("2024100001");
        activeEmployee.setStatus(Status.ACTIVE);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(activeEmployee));

        //when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(id);

        //then
        assertNotNull(userDetails);
        assertEquals(id, userDetails.getUsername());

        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
    }

    @Test
    @DisplayName("활성화되지 않는 사용자일 때 예외 발생 테스트")
    void loadUserByUsername_userNotFound() throws Exception {
        //given
        String id = "999";

        // Mock에서 빈 값을 반환하도록 설정
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        //when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(id);
        });
    }


}