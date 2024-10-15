package com.hq.heroes.salary.repository;

import com.hq.heroes.salary.entity.Deduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeductRepository extends JpaRepository<Deduct, Long> {
    List<Deduct> findAll();

}
