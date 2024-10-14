package com.hq.heroes.employee.repository;

import com.hq.heroes.employee.dto.PositionDTO;
import com.hq.heroes.employee.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position,Long> {

    @Query("SELECT new com.hq.heroes.employee.dto.PositionDTO(p.positionId, p.positionName) FROM Position p")
    List<PositionDTO> findAllPositions(); // 모든 직책을 가져오는 메서드

}
