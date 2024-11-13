package com.hq.heroes.event.repository;

import com.hq.heroes.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // 사원 번호로 사원의 일정 조회
    List<Event> findByEmployee_EmployeeId(String employeeId);

}
