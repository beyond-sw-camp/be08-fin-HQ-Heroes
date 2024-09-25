package com.hq.heroes.auth.service;

import com.hq.heroes.auth.dto.oAuth2.*;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String clientName = userRequest.getClientRegistration().getClientName();

        OAuth2Response response = null;
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (clientName.equals("naver")) {
            response = new NaverResponse(attributes);
        } else if (clientName.equals("google")) {
            response = new GoogleResponse(attributes);
        } else {
            return null;
        }

        String email = response.getEmail();
        Optional<Employee> isExist = employeeRepository.findByEmail(email);

        if (isExist.isPresent() && isExist.get().getStatus() == Status.INACTIVE) {
            employeeRepository.deleteById(isExist.get().getEmployeeId());
        }

        saveOrUpdateUser(response, email);

        OAuth2EmployeeDto oAuth2EmployeeDto = OAuth2EmployeeDto.builder()
                .name(response.getName())
                .email(email)
                .role("ROLE_USER")
                .build();

        return new CustomOAuth2Employee(oAuth2EmployeeDto);
    }

    private void saveOrUpdateUser(OAuth2Response response, String email) {
        Optional<Employee> isExist = employeeRepository.findByEmail(email);

        if (isExist.isPresent()) {
            Employee employee = isExist.get();
            employee.setEmployeeName(response.getName());
            employee.setEmail(response.getEmail());
            employeeRepository.save(employee);
        } else {
            Employee employee = new Employee();
            employee.setEmail(email);
            employee.setEmployeeName(response.getName());
            employee.setPassword("OAuth2 인증 사용자");
            employeeRepository.save(employee);
        }
    }
}

