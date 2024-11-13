package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.DeductDTO;

import java.util.List;

public interface DeductService {

    // 모든 공제 조회
    List<DeductDTO> getAllDeducts();
}
