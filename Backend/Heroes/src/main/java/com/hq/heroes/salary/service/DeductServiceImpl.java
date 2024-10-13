package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.DeductDTO;
import com.hq.heroes.salary.repository.DeductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeductServiceImpl implements DeductService {
    private final DeductRepository deductRepository;

    @Override
    public List<DeductDTO> getAllDeducts() {
        return deductRepository.findAll().stream()
                .map(d -> DeductDTO.builder()
                        .deductId(d.getDeductId())
                        .deductName(d.getDeductName())
                        .deductionRate(d.getDeduction_rate())
                        .description(d.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
