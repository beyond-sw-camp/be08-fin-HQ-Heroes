package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.DeductDTO;

import java.util.List;

public interface DeductService {
    List<DeductDTO> getAllDeducts(); // 모든 공제 조회
}
