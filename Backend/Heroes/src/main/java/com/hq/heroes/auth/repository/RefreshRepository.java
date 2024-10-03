package com.hq.heroes.auth.repository;

import com.hq.heroes.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    @Query("SELECT r.employeeId FROM RefreshToken r WHERE r.refresh = :refresh")
    String findEmailByRefresh(@Param("refresh") String refresh);
}
