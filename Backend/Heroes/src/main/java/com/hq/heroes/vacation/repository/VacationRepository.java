package com.hq.heroes.vacation.repository;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {

    // employee의 id로 승인된 휴가 데이터를 조회할 때
    @Query("SELECT v FROM Vacation v WHERE v.employee.employeeId = :employeeId AND v.vacationStatus = 'APPROVED'")
    List<Vacation> findApprovedVacationsByEmployeeId(@Param("employeeId") String employeeId);

    // 팀 ID를 사용하여 해당 팀의 모든 휴가 정보를 조회하는 쿼리
    @Query("SELECT v FROM Vacation v WHERE v.employee.team.teamId = :teamId")
    List<Vacation> findVacationsByTeamId(@Param("teamId") Long teamId);

    // 사원 아이디로 휴가 기간과 상태를 확인해서 존재하는지 판단
    boolean existsByEmployeeAndVacationStartDateLessThanEqualAndVacationEndDateGreaterThanEqualAndVacationStatusNot(
            Employee employee, LocalDate vacationEndDate, LocalDate vacationStartDate, VacationStatus cancelApproved);

    List<Vacation> findByEmployeeEmployeeIdAndVacationStatus(String employeeId, VacationStatus vacationStatus);
}
