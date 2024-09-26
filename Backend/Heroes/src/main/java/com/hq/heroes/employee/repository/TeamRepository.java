package com.hq.heroes.employee.repository;

import com.hq.heroes.employee.dto.TeamDTO;
import com.hq.heroes.employee.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {

    @Query("SELECT new com.hq.heroes.employee.dto.TeamDTO(t.teamName) FROM Team t")
    List<TeamDTO> findAllTeams();

}
