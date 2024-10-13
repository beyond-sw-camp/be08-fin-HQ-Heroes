package com.hq.heroes.vacation.repository;

import com.hq.heroes.vacation.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {

    // employee의 id로 승인된 휴가 데이터를 조회할 때
    @Query("SELECT v FROM Vacation v WHERE v.employee.employeeId = :employeeId AND v.vacationStatus = 'APPROVED'")
    List<Vacation> findApprovedVacationsByEmployeeId(@Param("employeeId") String employeeId);
}
