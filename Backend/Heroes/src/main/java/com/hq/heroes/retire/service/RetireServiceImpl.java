package com.hq.heroes.retire.service;

import com.hq.heroes.retire.dto.RetireResponseDTO;
import com.hq.heroes.retire.entity.Retire;
import com.hq.heroes.retire.repository.RetireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RetireServiceImpl implements RetireService {

    private final RetireRepository retireRepository;

    @Override
    public RetireResponseDTO getRetirementPayByEmployeeId(String employeeId) {
        Retire retireEntity = retireRepository.findByEmployee_EmployeeId(employeeId);
        if (retireEntity != null) {
            return new RetireResponseDTO(
                    retireEntity.getPayrollAvg(),
                    retireEntity.getAnnualBonus(),
                    retireEntity.getPeriod()
            );
        }
        return null; // 직원 ID에 해당하는 데이터가 없을 경우
    }

    @Override
    public RetireResponseDTO updateRetirementPay(String employeeId, RetireResponseDTO retireResponseDTO) {
        Retire retireEntity = retireRepository.findByEmployee_EmployeeId(employeeId);
        if (retireEntity != null) {
            retireEntity.setPayrollAvg(retireResponseDTO.getPayrollAvg());
            retireEntity.setAnnualBonus(retireResponseDTO.getAnnualBonus());
            retireEntity.setPeriod(retireResponseDTO.getPeriod());
            retireRepository.save(retireEntity);
            return retireResponseDTO; // 수정된 DTO 반환
        }
        return null; // 직원 ID에 해당하는 데이터가 없을 경우
    }

    @Override
    public boolean deleteRetirementPay(String employeeId) {
        Retire retireEntity = retireRepository.findByEmployee_EmployeeId(employeeId);
        if (retireEntity != null) {
            retireRepository.delete(retireEntity);
            return true; // 삭제 성공
        }
        return false; // 직원 ID에 해당하는 데이터가 없을 경우
    }
}
