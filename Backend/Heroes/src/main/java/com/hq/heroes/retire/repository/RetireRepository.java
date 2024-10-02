package com.hq.heroes.retire.repository;

import com.hq.heroes.retire.entity.Retire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetireRepository extends JpaRepository<Retire, String> {
    Retire findByEmployee_EmployeeId(String employeeId);
}
