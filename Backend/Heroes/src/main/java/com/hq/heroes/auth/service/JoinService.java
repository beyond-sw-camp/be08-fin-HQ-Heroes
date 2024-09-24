package com.hq.heroes.auth.service;


import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final EmployeeRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String join(JoinDTO joinDTO) throws Exception {
        String email = joinDTO.getEmail();

        // Check if the email already exists
        if (userRepository.existsByEmail(email)) {
            if (!userRepository.findByEmail(email).get().getStatus().equals(Status.N)) {
                throw new Exception("존재하는 회원입니다.");
            } else {
                userRepository.deleteById(userRepository.findByEmail(email).get().getEmployeeId());
            }
        }

        // Proceed with the registration
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        employee.setName(joinDTO.getName());
        employee.setStatus(Status.Y);
        employee.setRole(Role.ROLE_USER);

        userRepository.save(employee);
        return employee.getName() + "님 환영합니다!";
    }

}
